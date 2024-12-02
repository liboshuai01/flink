package cn.liboshuai.flink.common.rpc;

public class RpcEndpoint {

    /**
     * RPC 服务用于启动 RPC 服务器并获取 RPC 网关。
     */
    private RpcService rpcService;

    /**
     * 此 RPC 端点的唯一标识符。
     */
    private String endpointId;

    public RpcEndpoint(final RpcService rpcService, final String endpointId) {
        this.rpcService = rpcService;
        this.endpointId = endpointId;
    }

    public RpcService getRpcService() {
        return rpcService;
    }

    public String getEndpointId() {
        return endpointId;
    }
}
