package cn.liboshuai.flink.server;

import cn.liboshuai.flink.common.rpc.Configuration;
import cn.liboshuai.flink.common.rpc.RpcService;
import cn.liboshuai.flink.common.rpc.RpcUtils;

public class JobMasterRunner {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setProperty("actor.system.name", "jobmanager");
        RpcService rpcService = RpcUtils.createRpcService(configuration);
        JobMaster jobMaster = new JobMaster(rpcService);
    }
}
