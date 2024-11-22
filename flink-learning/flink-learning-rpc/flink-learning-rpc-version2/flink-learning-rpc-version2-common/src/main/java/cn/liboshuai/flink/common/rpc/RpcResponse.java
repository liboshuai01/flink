package cn.liboshuai.flink.common.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RpcResponse类，用于表示RPC响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;      // 响应状态码
    private String msg;    // 响应消息
    private Object data;       // 响应数据
}
