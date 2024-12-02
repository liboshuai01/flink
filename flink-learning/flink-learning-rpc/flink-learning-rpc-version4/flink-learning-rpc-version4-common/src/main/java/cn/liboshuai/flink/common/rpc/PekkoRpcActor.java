package cn.liboshuai.flink.common.rpc;

import cn.liboshuai.flink.common.gateway.RpcGateway;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 接受 actor 请求，可以来自于 remote，也可能是 local
 * 一个 actor 实例，对应一个 endpoint 实例
 */
@Slf4j
public class PekkoRpcActor<T extends RpcEndpoint & RpcGateway> extends AbstractActor implements RpcActor {

    final T endpoint;

    public PekkoRpcActor(T endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(RpcInvoke.class, this::invokeRpc)
                .build();
    }


    private void invokeRpc(RpcInvoke rpcInvoke) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ActorRef sender = getSender();
        log.info("收到 rpc 调用请求, 请求方: {}", sender);

        String methodName = rpcInvoke.getMethodName();
        Class<?>[] parameterTypes = rpcInvoke.getParameterTypes();
        Object[] params = rpcInvoke.getParams();

        Method method = endpoint.getClass().getMethod(methodName, parameterTypes);
        Object invokeResult = method.invoke(endpoint, params);

        // 处理细节：目标方法可能是 void，也可能是有返回值的
        if (Objects.equals(method.getReturnType(), Void.TYPE)) {
            log.info("没有返回值");
        } else {
            log.info("准备返回结果：{}", invokeResult);
            sender.tell(invokeResult, getSelf());
        }

    }
}
