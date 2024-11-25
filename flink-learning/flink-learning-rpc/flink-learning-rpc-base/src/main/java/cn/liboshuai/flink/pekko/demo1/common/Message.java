package cn.liboshuai.flink.pekko.demo1.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息类，用于在客户端和服务端之间传递数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L; // 用于序列化版本控制

    private String data; // 消息内容
}
