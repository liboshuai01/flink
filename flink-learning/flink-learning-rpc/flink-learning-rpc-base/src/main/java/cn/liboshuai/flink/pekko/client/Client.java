package cn.liboshuai.flink.pekko.client;

import cn.liboshuai.flink.pekko.client.actor.ClientActor;
import cn.liboshuai.flink.pekko.client.pojo.Start;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Client {
    public static void main(String[] args) {
        Map<String,Object> overrides = new HashMap<>();
        overrides.put("pekko.remote.artery.canonical.port", 9994);
        Config config = ConfigFactory.parseMap(overrides).withFallback(ConfigFactory.load());
        ActorSystem actorSystem = ActorSystem.create("client",config);
        ActorRef actorRef = actorSystem.actorOf(Props.create(ClientActor.class), "client-actor");
        actorRef.tell(new Start(), ActorRef.noSender());
    }
}
