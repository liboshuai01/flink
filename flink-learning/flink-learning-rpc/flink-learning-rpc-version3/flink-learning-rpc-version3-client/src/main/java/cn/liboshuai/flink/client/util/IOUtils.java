package cn.liboshuai.flink.client.util;

import cn.liboshuai.flink.common.rpc.RpcRequest;
import cn.liboshuai.flink.common.rpc.RpcResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.pattern.Patterns;

import org.apache.pekko.util.Timeout;

import scala.concurrent.Await;
import scala.concurrent.Future;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class IOUtils {
    private static final ActorRef actorRef;

    static {
        // 设置客户端的配置
        Config config = ConfigFactory.load();
        ActorSystem actorSystem = ActorSystem.create("client", config); // 创建Actor系统
        // 获取服务端Actor的引用
        ActorSelection actorSelection = actorSystem.actorSelection(
                "pekko://server@127.0.0.1:10003/user/server-actor");
        CompletableFuture<ActorRef> completableFuture = actorSelection
                .resolveOne(Duration.ofSeconds(1))
                .toCompletableFuture();

        // 异常处理，确保在获取Actor引用时处理可能的异常
        try {
            actorRef = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static RpcResponse sendRpcRequest(RpcRequest rpcRequest) throws Exception {
        Timeout timeout = Timeout.create(Duration.ofSeconds(5));
        Future<Object> future = Patterns.ask(actorRef, rpcRequest, timeout);
        return  (RpcResponse) Await.result(future, timeout.duration());
    }
}
