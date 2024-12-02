package cn.liboshuai.flink.client;

import cn.liboshuai.flink.common.rpc.Configuration;
import cn.liboshuai.flink.common.rpc.RpcService;
import cn.liboshuai.flink.common.rpc.RpcUtils;

public class TaskExecutorRunner {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setProperty("actor.system.name", "taskmanager");
        RpcService rpcService = RpcUtils.createRpcService(configuration);
        TaskExecutor jobMaster = new TaskExecutor(rpcService);
    }
}
