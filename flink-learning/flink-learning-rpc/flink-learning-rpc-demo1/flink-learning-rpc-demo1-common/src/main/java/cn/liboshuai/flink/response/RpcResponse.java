package cn.liboshuai.flink.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    public static String SUCCEED = "succeed";
    public static String FAILED = "failed";

    /**
     * 响应状态: succeed-成功；failed-失败
     */
    private String status;
    /**
     * 异常信息
     */
    private String msg;
    /**
     * 响应数据
     */
    private Object data;
}
