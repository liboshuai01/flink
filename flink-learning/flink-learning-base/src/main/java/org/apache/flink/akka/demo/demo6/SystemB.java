package org.apache.flink.akka.demo.demo6;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.japi.pf.ReceiveBuilder;


import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SystemB {

    public static void main(String[] args) {
        // 创建一个 HashMap 来存储配置覆盖项
        HashMap<String, Object> overrides = new HashMap<>();
        // 指定 Actor 系统的远程端口
        overrides.put("pekko.remote.artery.canonical.port", 17336);

        // 解析配置并与默认配置合并
        Config config = ConfigFactory.parseMap(overrides).withFallback(ConfigFactory.load());

        // 创建名为 "system-b" 的 Actor 系统
        ActorSystem actorSystem = ActorSystem.create("system-b", config);

        // 创建一个名为 "b-actor" 的 Actor 实例
        ActorRef actorRef = actorSystem.actorOf(Props.create(SystemB_Actor.class), "b-actor");

        // 向 b-actor 发送 Start 消息以启动处理
        actorRef.tell(new Start(), ActorRef.noSender());
    }
}

// 定义 SystemB 中的 Actor
class SystemB_Actor extends AbstractActor {

    @Override
    public Receive createReceive() {
        // 定义该 Actor 可以处理的消息类型
        return ReceiveBuilder.create()
                .match(Start.class, this::start) // 处理 Start 类型的消息
                .match(SystemMsg.class, this::handleSystemMsg) // 处理 SystemMsg 类型的消息
                .build();
    }

    // 处理 Start 消息，发送消息到 SystemA 的 a-actor
    public void start(Start start) throws ExecutionException, InterruptedException {
        // 使用 ActorSelection 查找远程 Actor
        ActorSelection actorSelection = getContext().actorSelection("pekko://system-a@127.0.0.1:17330/user/a-actor");

        // 异步解析 Actor 引用
        CompletableFuture<ActorRef> completableFuture = actorSelection.resolveOne(Duration.ofSeconds(1)).toCompletableFuture();

        // 获取 Actor 引用
        ActorRef actorRef = completableFuture.get();

        // 向 a-actor 发送 SystemMsg 消息
        actorRef.tell(new SystemMsg("我是系统B的actor"), getSelf());
    }

    // 处理来自 SystemA 的 SystemMsg 消息
    public void handleSystemMsg(SystemMsg msg) {
        // 打印收到的消息内容
        System.out.println("B系统收到了a系统的消息: " + msg.getMsg());
    }
}

// 定义 Start 消息类
class Start {

}
