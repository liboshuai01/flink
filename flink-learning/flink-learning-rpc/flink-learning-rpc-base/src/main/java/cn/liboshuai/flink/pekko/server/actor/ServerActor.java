package cn.liboshuai.flink.pekko.server.actor;

import cn.liboshuai.flink.pekko.common.Message;
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
                .match(Message.class, this::handleMessage) // 匹配消息类型
                .build();
    }

    /**
     * 处理接收到的消息。
     *
     * @param message 接收到的消息
     */
    private void handleMessage(Message message) {
        ActorRef sender = getSender(); // 获取发送者引用
        log.info("Received message: {}, from: {}", message, sender);
        // 回复发送者
        sender.tell(Message.builder().data("我是服务端-" + System.currentTimeMillis()).build(), getSelf());
    }
}
