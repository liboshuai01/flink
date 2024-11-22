package cn.liboshuai.flink.pekko.client.actor;

import cn.liboshuai.flink.pekko.client.pojo.Start;
import cn.liboshuai.flink.pekko.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ClientActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Start.class,this::handleStart)
                .match(Message.class,this::handleMessage)
                .build();
    }

    private void handleMessage(Message message) {
        log.info("receive message: {}, from: {}", message, getSender());
    }

    private void handleStart(Start start) throws ExecutionException, InterruptedException {
        ActorSelection actorSelection = getContext().actorSelection(
                "pekko://server@127.0.0.1:9993/user/server-actor");
        CompletableFuture<ActorRef> completableFuture = actorSelection
                .resolveOne(Duration.ofSeconds(1))
                .toCompletableFuture();
        ActorRef actorRef = completableFuture.get();
        actorRef.tell(new Message("我是客户端-" + System.currentTimeMillis()), getSelf());
    }
}
