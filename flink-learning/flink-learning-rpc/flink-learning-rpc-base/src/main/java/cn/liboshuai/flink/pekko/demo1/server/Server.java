package cn.liboshuai.flink.pekko.demo1.server;

import cn.liboshuai.flink.pekko.demo1.server.actor.ServerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

/**
 * 服务端主类，启动Actor系统和服务端Actor。
 */
@Slf4j
public class Server {
    public static void main(String[] args) {
        Config config = ConfigFactory.load(); // 加载配置
        ActorSystem actorSystem = ActorSystem.create("server", config); // 创建Actor系统
        actorSystem.actorOf(Props.create(ServerActor.class), "server-actor"); // 创建服务端Actor
    }
}
