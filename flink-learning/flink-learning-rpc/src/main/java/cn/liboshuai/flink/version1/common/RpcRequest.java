package cn.liboshuai.flink.version1.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RpcRequest类，用于表示RPC请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String interfaceName; // 接口名称
    private String methodName;     // 方法名称
    private Object[] params;       // 方法参数
    private Class<?>[] parameterTypes; // 参数类型
}
