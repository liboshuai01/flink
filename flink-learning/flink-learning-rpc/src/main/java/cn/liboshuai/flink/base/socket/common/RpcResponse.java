package cn.liboshuai.flink.base.socket.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    private int code;
    private String msg;
    private T data;
}
