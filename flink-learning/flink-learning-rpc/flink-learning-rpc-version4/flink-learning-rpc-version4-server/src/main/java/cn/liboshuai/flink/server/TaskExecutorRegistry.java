package cn.liboshuai.flink.server;

import cn.liboshuai.flink.common.gateway.TaskExecutorGateway;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutorRegistry {
    private String resourceId;
    private String address;
    private TaskExecutorGateway taskExecutorGateway;
}
