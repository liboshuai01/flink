package cn.liboshuai.flink.common.gateway;

public interface TaskExecutorGateway extends RpcGateway {
    String queryState();

    String submitTask(String task);
}
