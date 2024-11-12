package org.apache.flink.akka;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 6784384773410534807L;

    /**
     * 响应状态
     */
    private String status = "0";
    /**
     * 响应信息，如异常信息
     */
    private String message;

    /**
     * 响应数据，返回值
     */
    private Object data;


    public String getStatus() {
        return status;
    }

    public RpcResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RpcResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RpcResponse setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
