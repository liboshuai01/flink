package cn.liboshuai.flink.pekko.client.actor;

import cn.liboshuai.flink.pekko.client.pojo.Start;
import cn.liboshuai.flink.pekko.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.japi.pf.ReceiveBuilder;
import org.apache.pekko.pattern.Patterns;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 客户端Actor，负责发送消息到服务端并处理回复。
 */
@Slf4j
public class ClientActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Start.class, this::handleStart) // 匹配启动消息
                .match(Message.class, this::handleMessage) // 匹配回复消息
                .build();
    }

    /**
     * 处理接收到的消息。
     *
     * @param message 接收到的消息
     */
    private void handleMessage(Message message) {
        log.info("Received message: {}, from: {}", message, getSender());
    }

    /**
     * 处理启动消息，发送初始消息到服务端。
     *
     * @param start 启动消息
     */
    private void handleStart(Start start) {
        // 获取服务端Actor的引用
        ActorSelection actorSelection = getContext().actorSelection(
                "pekko://server@127.0.0.1:9993/user/server-actor");
        CompletableFuture<ActorRef> completableFuture = actorSelection
                .resolveOne(Duration.ofSeconds(1))
                .toCompletableFuture();

        // 异常处理，确保在获取Actor引用时处理可能的异常
        try {
            ActorRef actorRef = completableFuture.get();
            if (actorRef != null) {
                actorRef.tell(new Message("我是客户端-" + System.currentTimeMillis()), getSelf());
            } else {
                log.error("Failed to resolve ActorRef.");
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while resolving ActorRef: ", e);
        }
    }
}
