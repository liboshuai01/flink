package cn.liboshuai.flink.pekko.demo2.server.actor;

import cn.liboshuai.flink.pekko.demo2.common.RpcRequest;
import cn.liboshuai.flink.pekko.demo2.common.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.japi.pf.ReceiveBuilder;

/**
 * 服务端Actor，处理接收到的消息并回复客户端。
 */
@Slf4j
public class ServerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(RpcRequest.class, this::handleRpcRequest) // 匹配消息类型
                .build();
    }

    /**
     * 处理接收到的消息。
     */
    private void handleRpcRequest(RpcRequest rpcRequest) {
        ActorRef sender = getSender(); // 获取发送者引用
        log.info("Received rpcRequest: {}, from: {}", rpcRequest, sender);
        // 回复发送者
        RpcResponse rpcResponse = RpcResponse.builder()
                .code(200)
                .data("这是响应结果")
                .build();
        sender.tell(rpcResponse, getSelf());
    }
}
