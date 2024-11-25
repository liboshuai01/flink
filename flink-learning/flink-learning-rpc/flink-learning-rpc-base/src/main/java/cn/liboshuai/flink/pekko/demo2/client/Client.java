package cn.liboshuai.flink.pekko.demo2.client;

import cn.liboshuai.flink.pekko.demo2.common.RpcRequest;
import cn.liboshuai.flink.pekko.demo2.common.RpcResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.pattern.Patterns;
import org.apache.pekko.util.Timeout;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import scala.concurrent.Await;
import scala.concurrent.Future;

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
        // 获取服务端Actor的引用
        ActorSelection actorSelection = actorSystem.actorSelection(
                "pekko://server@127.0.0.1:9993/user/server-actor");
        CompletableFuture<ActorRef> completableFuture = actorSelection
                .resolveOne(Duration.ofSeconds(1))
                .toCompletableFuture();

        // 异常处理，确保在获取Actor引用时处理可能的异常
        try {
            ActorRef actorRef = completableFuture.get();
            if (actorRef != null) {
                Timeout timeout = Timeout.create(Duration.ofSeconds(5));
                RpcRequest rpcRequest = RpcRequest.builder()
                        .interfaceName("xxx")
                        .methodName("xxx")
                        .params(new Object[]{"xxx"})
                        .parameterTypes(new Class<?>[]{String.class})
                        .build();
                Future<Object> future = Patterns.ask(actorRef, rpcRequest, timeout);
                RpcResponse rpcResponse = (RpcResponse) Await.result(future, timeout.duration());
                log.info("Received rpcResponse: {}, from: {}", rpcResponse, actorRef);
            } else {
                log.error("Failed to resolve ActorRef.");
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while resolving ActorRef: ", e);
        }
    }
}
