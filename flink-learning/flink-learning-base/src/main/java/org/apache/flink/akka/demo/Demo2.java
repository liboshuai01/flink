package org.apache.flink.akka.demo;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo2 {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("my-system");
        AtomicInteger counter = new AtomicInteger(0);

        System.out.println(System.currentTimeMillis() + ": 任务开始");

        for (int i = 0; i < 1000000; i++) {
            ActorRef actorRef = actorSystem.actorOf(
                    Props.create(AdderActor.class, counter),
                    i + "");
            actorRef.tell(new Add(), ActorRef.noSender());
        }

        while(true) {
            if (counter.get() == 1000000) {
                System.out.println(System.currentTimeMillis() + ": 任务结束");
                break;
            }
        }
    }
}

class AdderActor extends AbstractActor {

    AtomicInteger counter;

    public AdderActor(AtomicInteger counter) {
        this.counter = counter;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Add.class, this::handleAdd)
                .build();
    }

    public void handleAdd(Add add) {
        counter.incrementAndGet();
    }
}


class Add {

}
