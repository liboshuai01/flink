package cn.liboshuai.flink.pekko.demo1.client;

import cn.liboshuai.flink.pekko.demo1.client.actor.ClientActor;
import cn.liboshuai.flink.pekko.demo1.client.pojo.Start;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端主类，启动Actor系统和客户端Actor。
 */
@Slf4j
public class Client {
    public static void main(String[] args) {
        // 设置客户端的配置
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("pekko.remote.artery.canonical.port", 9994);
        Config config = ConfigFactory.parseMap(overrides).withFallback(ConfigFactory.load());

        ActorSystem actorSystem = ActorSystem.create("client", config); // 创建Actor系统
        ActorRef actorRef = actorSystem.actorOf(Props.create(ClientActor.class), "client-actor"); // 创建客户端Actor
        actorRef.tell(new Start(), ActorRef.noSender()); // 发送启动消息
    }
}
