package cn.liboshuai.flink.client.handler;

import cn.liboshuai.flink.client.util.IOUtils;
import cn.liboshuai.flink.common.rpc.RpcRequest;
import cn.liboshuai.flink.common.rpc.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

@Slf4j
public class RpcHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        RpcResponse rpcResponse = IOUtils.sendRpcRequest(rpcRequest);
        if (Objects.isNull(rpcResponse)) {
            return null;
        }
        if (rpcResponse.getCode() != 200) {
            throw new RuntimeException("rpc request fail, msg: " + rpcResponse.getMsg());
        }
        return rpcResponse.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object object = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) object;
    }
}
