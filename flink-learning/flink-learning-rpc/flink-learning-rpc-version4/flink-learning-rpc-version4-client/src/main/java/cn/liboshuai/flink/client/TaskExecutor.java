package cn.liboshuai.flink.client;

import cn.liboshuai.flink.common.gateway.TaskExecutorGateway;
import cn.liboshuai.flink.common.rpc.RpcEndpoint;
import cn.liboshuai.flink.common.rpc.RpcService;

public class TaskExecutor extends RpcEndpoint implements TaskExecutorGateway {

    public TaskExecutor(RpcService rpcService) {
        // 调用父类构造，以准备各种rpc基础功能
        super(rpcService, "taskmanager");
        // 模块自身的相关构造逻辑
    }

    /**
     * 查询 task 状态的 rpc 方法
     */
    @Override
    public String queryState() {

        return "some state from executor";
    }

    /**
     * 提交 task 任务的 rpc 方法
     */
    @Override
    public String submitTask(String task) {

        return "submit task success from executor";
    }
}
