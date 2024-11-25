package cn.liboshuai.flink.server.actor;

import cn.liboshuai.flink.common.rpc.RpcRequest;
import cn.liboshuai.flink.common.rpc.RpcResponse;
import cn.liboshuai.flink.server.service.OrderServiceImpl;
import cn.liboshuai.flink.server.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.japi.pf.ReceiveBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端Actor，处理接收到的消息并回复客户端。
 */
@Slf4j
public class ServerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(RpcRequest.class, this::handleRpcRequest) // 匹配消息类型
                .build();
    }

    /**
     * 处理接收到的消息。
     */
    private void handleRpcRequest(RpcRequest rpcRequest) throws Exception {
        log.info("接收到请求: {}", rpcRequest);

        Map<String, Class<?>> interfaceServiceImplMap = new HashMap<>();
        interfaceServiceImplMap.put(OrderServiceImpl.class.getInterfaces()[0].getName(), OrderServiceImpl.class);
        interfaceServiceImplMap.put(UserServiceImpl.class.getInterfaces()[0].getName(), UserServiceImpl.class);

        ActorRef sender = getSender(); // 获取发送者引用
        // 通过反射调用方法
        Class<?> aClass = interfaceServiceImplMap.get(rpcRequest.getInterfaceName());
        Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object newInstance = aClass.getDeclaredConstructor().newInstance(); // 使用getDeclaredConstructor()以确保可以创建实例
        Object result =  method.invoke(newInstance, rpcRequest.getParams());

        // 创建并发送RPC响应
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setCode(200);
        rpcResponse.setData(result);

        sender.tell(rpcResponse, getSelf());
    }
}
