package org.apache.flink.akka;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaUtils {
    public static ActorSystem createRemoteActorSystem(String name, int port) {
        String systemConfigStr = "akka.actor.provider = \"akka.remote.RemoteActorRefProvider\"\r\n" +
                "akka.remote.enabled-transports=[\"akka.remote.netty.tcp\"]\r\n" +
                "akka.remote.netty.tcp.hostname = \"0.0.0.0\"\r\n" +
                "akka.remote.netty.tcp.port = \"" + port + "\"";
        Config systemConfig = ConfigFactory.parseString(systemConfigStr);
        return ActorSystem.create(name, systemConfig);
    }
}
