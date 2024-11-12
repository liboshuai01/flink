package org.apache.flink.akka;

import akka.actor.UntypedAbstractActor;

import org.apache.flink.util.ThreadUtils;

import java.lang.reflect.Method;

public class AkkaRpcServerActor<T> extends UntypedAbstractActor {
    private final T ref;
    private final Class<?> interfaceClass;


    public AkkaRpcServerActor(T ref, Class<?> interfaceClass) {
        this.ref = ref;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) message;
            ThreadUtils.log("Received request:{}", request);
            // 处理请求
            RpcResponse response = handleRequest(request);
            // 将结果返回给客户端
            ThreadUtils.log("Send response to client.{}", response);
            getSender().tell(response, getSelf());
        }
    }

    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        try {
            ThreadUtils.log("The server is handling request.");
            Method method = interfaceClass.getMethod(request.getMethodName(), request.getParameterTypes());
            Object data = method.invoke(ref, request.getParameters());
            response.setData(data);
        } catch (Exception e) {
            response.setStatus("1").setMessage(e.getMessage());
        }
        return response;
    }
}
