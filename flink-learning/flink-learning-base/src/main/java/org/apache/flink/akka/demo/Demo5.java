package org.apache.flink.akka.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;

public class Demo5 {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("my-system");

        // Actor[pekko://my-system/user/actor-1#1142358025]
        ActorRef actorRef1 = actorSystem.actorOf(Props.create(ActorA.class), "actor-1");

        // Actor[pekko://my-system/user/actor-2#334281967]
        ActorRef actorRef2 = actorSystem.actorOf(Props.create(ActorA.class), "actor-2");

        actorRef1.tell(new Msg("你好"), actorRef2);
    }
}

class ActorA extends AbstractActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Msg.class, this::handleMsg)
                .build();
    }

    private void handleMsg(Msg msg) throws InterruptedException {
        ActorRef sender = getSender();
        System.out.println("收到来自于 [" + sender + "] 的消息: " + msg.toString());
        Thread.sleep(1000);
        sender.tell(new Msg("回应-" + msg.getMsg()), getSelf());
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Msg{
    private String msg;
}
