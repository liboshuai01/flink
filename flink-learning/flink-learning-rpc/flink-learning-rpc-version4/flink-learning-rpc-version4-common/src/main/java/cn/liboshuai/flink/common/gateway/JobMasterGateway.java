package cn.liboshuai.flink.common.gateway;

import java.util.concurrent.CompletableFuture;

public interface JobMasterGateway extends RpcGateway{

    String registerTaskExecutor(String resourceId, String taskExecutorAddress);

    CompletableFuture<String> getMasterId();
}
