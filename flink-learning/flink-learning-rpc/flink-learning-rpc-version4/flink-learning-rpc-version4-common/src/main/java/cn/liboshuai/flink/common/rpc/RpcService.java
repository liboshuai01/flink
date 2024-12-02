package cn.liboshuai.flink.common.rpc;

import cn.liboshuai.flink.common.gateway.RpcGateway;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RpcService {
    ActorSystem actorSystem;

    Map<String, ActorRef> actorRefMap = new HashMap<>();

    public RpcService(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    /**
     * 获取远程 actor 的引用，并构造一个远程 endpoint 的 gateway 动态代理对象
     */
    public <G extends RpcGateway> G connect(String address, Class<G> gatewayClass) {


        return null;
    }

    /**
     * 根据传入的功能实体对象，创建对应的 actor
     * 并创建出功能实体类 endpoint 的动态代理对象
     */
    public <C extends RpcEndpoint & RpcGateway> void startServer(C rpcEndpoint) {
        ActorRef actorRef = actorSystem.actorOf(
                Props.create(PekkoRpcActor.class),
                rpcEndpoint.getEndpointId());

        // 为了后续的管理方便，可以把这个创建好的 endpoint 对应的 actor 放入一个集合中
        actorRefMap.put(rpcEndpoint.getEndpointId(), actorRef);

        // 抽取 endpoint 对象类上的实现的接口
        Class<?>[] interfaces = rpcEndpoint.getClass().getInterfaces();

        // 生成一个自身 endpoint 的动态代理对象
        Object o = Proxy.newProxyInstance(
                RpcService.class.getClassLoader(),
                interfaces,
                new PekkoInvocationHandler(actorRef));
    }
}
