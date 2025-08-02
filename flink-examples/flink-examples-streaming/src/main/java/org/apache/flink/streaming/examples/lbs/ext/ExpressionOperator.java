package org.apache.flink.streaming.examples.lbs.ext;

import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.jobgraph.tasks.TaskOperatorEventGateway;
import org.apache.flink.runtime.operators.coordination.CoordinationRequest;
import org.apache.flink.runtime.operators.coordination.CoordinationResponse;
import org.apache.flink.streaming.api.graph.StreamConfig;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.runtime.tasks.StreamTask;
import org.apache.flink.util.SerializedValue;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExpressionOperator extends AbstractStreamOperator<LbsEvent> implements OneInputStreamOperator<LbsEvent, LbsEvent> {

    // job master的rpc客户端
    TaskOperatorEventGateway coordinator;
    // 算子ID
    OperatorID operatorID;
    // 下游的并行度
    int downStreamParallelism;

    public ExpressionOperator(int downStreamParallelism) {
        this.downStreamParallelism = downStreamParallelism;
    }

    @Override
    public void setup(
            StreamTask<?, ?> containingTask,
            StreamConfig config,
            Output<StreamRecord<LbsEvent>> output) {
        super.setup(containingTask, config, output);
        // 拿到算子所属task对应的job master端的operatorCoordinator的rpc客户端
        coordinator = containingTask
                .getEnvironment()
                .getOperatorCoordinatorEventGateway();
        // 获取 算子ID
        operatorID = getOperatorID();
    }

    @Override
    public void processElement(StreamRecord<LbsEvent> element) throws Exception {
        LbsEvent lbsEvent = element.getValue();

        if (lbsEvent instanceof ExpressionEvent) {

            ExpressionEvent exp = (ExpressionEvent) lbsEvent;
            String expression = exp.getExpression();

            // 向 coordinator 请求表达式变更
            ExpressionChangeRequest expressionChangeRequest = new ExpressionChangeRequest(expression);
            CompletableFuture<CoordinationResponse> responseFuture = coordinator.sendRequestToCoordinator(
                    operatorID,
                    new SerializedValue<CoordinationRequest>(expressionChangeRequest));
            // 向下游广播一个 flush 通知
            broadcast(lbsEvent);

            // 向 coordinator 请求放开阻塞
            CoordinationResponse coordinationResponse = releaseRequest();
        }
        // 向下游传递数据
        partitionBy(lbsEvent);
    }
    
    private void broadcast(LbsEvent lbsEvent) {
        for (int i = 0; i < downStreamParallelism; i++) {
            PartitionedEvent partitionedEvent = new PartitionedEvent(i, lbsEvent);
            output.collect(new StreamRecord<>(partitionedEvent));
        }
    }

    private CoordinationResponse releaseRequest() throws IOException, ExecutionException, InterruptedException {
        ReleaseBlockRequest releaseBlockRequest = new ReleaseBlockRequest();
        CompletableFuture<CoordinationResponse> releaseFuture = coordinator.sendRequestToCoordinator(
                operatorID,
                new SerializedValue<CoordinationRequest>(releaseBlockRequest));
        return releaseFuture.get();
    }

    private void partitionBy(LbsEvent lbsEvent) {
        int partitionId = lbsEvent.hashCode() % downStreamParallelism & Integer.MAX_VALUE;
        PartitionedEvent partitionedEvent = new PartitionedEvent(partitionId, lbsEvent);
        output.collect(new StreamRecord<>(partitionedEvent));
    }
}
