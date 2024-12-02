package cn.liboshuai.flink.common.rpc;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.pattern.Patterns;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PekkoInvocationHandler implements InvocationHandler {

    ActorRef targetEndpointActorRef;

    public PekkoInvocationHandler(ActorRef targetEndpointActorRef) {
        this.targetEndpointActorRef = targetEndpointActorRef;
    }

    /**
     * 核心功能：拦截动态代理对象上的方法调用，转成向目标 endpoint 的 actor 发送 rpcInvoke 消息
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造 rpc 请求消息
        RpcInvoke rpcInvoke = new RpcInvoke(method.getName(), method.getParameterTypes(), args);

        Object result = null;
        // 判断目标方法是否有返回值
        if (Objects.equals(method.getReturnType(), Void.TYPE)) {
            targetEndpointActorRef.tell(rpcInvoke, ActorRef.noSender());
        } else {
            Future<Object> future = Patterns.ask(targetEndpointActorRef, rpcInvoke, 2000);
            // 将 scala 的 future，转成 java 的 CompletableFuture
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            future.onComplete(scalaTry -> {
                if (scalaTry.isSuccess()) {
                    completableFuture.complete(scalaTry.get());
                } else {
                    completableFuture.completeExceptionally(scalaTry.failed().get());
                }
                return null;
            }, ExecutionContext.global());
            // 判断目标方法的返回值是否为 CompletableFuture：是，则直接返回 ask 得到的 future；否，则返回 future.get() 得到的结果
            if (Objects.equals(method.getReturnType(), CompletableFuture.class)) {
                result = completableFuture;
            } else {
                result = completableFuture.get();
            }
        }
        return result;
    }
}
