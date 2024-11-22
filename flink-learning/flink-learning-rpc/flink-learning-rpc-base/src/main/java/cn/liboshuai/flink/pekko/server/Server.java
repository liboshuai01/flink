package cn.liboshuai.flink.pekko.server;

import cn.liboshuai.flink.pekko.server.actor.ServerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

@Slf4j
public class Server {


    public static void main(String[] args) {
        Config config = ConfigFactory.load();
        ActorSystem actorSystem = ActorSystem.create("server", config);
        actorSystem.actorOf(Props.create(ServerActor.class), "server-actor");
    }
}
