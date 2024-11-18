package org.apache.flink.akka.demo.demo6;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;

public class SystemA {

    public static void main(String[] args) {
        // 加载默认配置
        Config config = ConfigFactory.load();

        // 创建名为 "system-a" 的 Actor 系统
        ActorSystem actorSystem = ActorSystem.create("system-a", config);

        // 创建一个名为 "a-actor" 的 Actor 实例
        ActorRef actorRef = actorSystem.actorOf(Props.create(SystemA_Actor.class), "a-actor");
    }
}

// 定义 SystemA 中的 Actor
class SystemA_Actor extends AbstractActor {

    @Override
    public Receive createReceive() {
        // 定义该 Actor 可以处理的消息类型
        return ReceiveBuilder.create()
                .match(SystemMsg.class, this::handleSystemMsg) // 处理 SystemMsg 类型的消息
                .build();
    }

    // 处理收到的 SystemMsg 消息
    public void handleSystemMsg(SystemMsg msg) {
        // 获取发送者的 Actor 引用
        ActorRef sender = getSender();
        // 打印发送者和消息内容
        System.out.println("来自 " + sender + " 消息: " + msg.getMsg());
        // 回复发送者一条消息
        sender.tell(new SystemMsg("你好"), getSelf());
    }
}
