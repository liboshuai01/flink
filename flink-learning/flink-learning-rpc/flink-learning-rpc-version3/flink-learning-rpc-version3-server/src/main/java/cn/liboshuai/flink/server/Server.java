package cn.liboshuai.flink.server;

import cn.liboshuai.flink.server.actor.ServerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

@Slf4j
public class Server {

    public static void main(String[] args) {
        Config config = ConfigFactory.load(); // 加载配置
        ActorSystem actorSystem = ActorSystem.create("server", config); // 创建Actor系统
        actorSystem.actorOf(Props.create(ServerActor.class), "server-actor"); // 创建服务端Actor
    }
}
