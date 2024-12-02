package cn.liboshuai.flink.server;

import cn.liboshuai.flink.common.gateway.JobMasterGateway;
import cn.liboshuai.flink.common.rpc.RpcEndpoint;
import cn.liboshuai.flink.common.rpc.RpcService;
import cn.liboshuai.flink.common.gateway.TaskExecutorGateway;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class JobMaster extends RpcEndpoint implements JobMasterGateway {

    Map<String,TaskExecutorRegistry> aliveExecutor = new HashMap<>();

    public JobMaster(RpcService rpcService) {
        // 调用父类构造，以准备各种rpc基础功能
        super(rpcService, "jobmanager");
        // 模块自身的相关构造逻辑

    }

    /**
     * 提供给 taskExecutor 来调用的rpc方法
     */
    @Override
    public String registerTaskExecutor(String resourceId, String taskExecutorAddress) {
        if (aliveExecutor.containsKey(taskExecutorAddress)) {
            return "注册重复";
        }
        return registerInternal(resourceId, taskExecutorAddress);
    }

    @Override
    public CompletableFuture<String> getMasterId() {
        return CompletableFuture.supplyAsync(() -> "master-1");
    }

    protected String registerInternal(String resourceId, String taskExecutorAddress) {
        log.info("收到 taskExecutor 注册信息：{}", taskExecutorAddress);
        TaskExecutorGateway taskExecutor = getRpcService().connect(taskExecutorAddress, TaskExecutorGateway.class);
        aliveExecutor.put(resourceId, new TaskExecutorRegistry(resourceId, taskExecutorAddress, taskExecutor));
        return "注册通过";
    }

    /**
     * 查询 taskExecutor 状态
     */
    public String queryTaskExecutorState(String taskExecutorId) {
        TaskExecutorGateway taskExecutor = aliveExecutor
                .get(taskExecutorId)
                .getTaskExecutorGateway();
        String state = taskExecutor.queryState();
        log.info(state);
        return state;
    }

    /**
     * 提交 task 任务
     */
    public String submitTask(String task, String taskExecutorId) {
        TaskExecutorGateway taskExecutor = aliveExecutor
                .get(taskExecutorId)
                .getTaskExecutorGateway();
        String response = taskExecutor.submitTask(task);
        log.info(response);
        return response;
    }
}
