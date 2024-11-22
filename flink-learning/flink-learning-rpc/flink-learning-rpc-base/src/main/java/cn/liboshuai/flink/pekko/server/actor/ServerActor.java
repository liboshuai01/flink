package cn.liboshuai.flink.pekko.server.actor;

import cn.liboshuai.flink.pekko.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.japi.pf.ReceiveBuilder;

@Slf4j
public class ServerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Message.class, this::handleMessage)
                .build();
    }

    private void handleMessage(Message message) {
        ActorRef sender = getSender();
        log.info("receive message: {}, from: {}", message, sender);
        sender.tell(Message.builder().data("我是服务端-" + System.currentTimeMillis()).build(), getSelf());
    }
}
