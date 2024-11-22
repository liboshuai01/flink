package cn.liboshuai.flink.client;


import cn.liboshuai.flink.common.RpcRequest;
import cn.liboshuai.flink.common.RpcResponse;
import cn.liboshuai.flink.common.User;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 10001)) {
            log.info("已连接到服务端: {}", socket.getRemoteSocketAddress());

            // 创建输入输出流
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                // 创建RPC请求
                RpcRequest rpcRequest = RpcRequest.builder()
                        .interfaceName("cn.liboshuai.flink.server.service.UserService")
                        .methodName("findUserByNameAndAge")
                        .parameterTypes(new Class[]{String.class, int.class})
                        .params(new Object[]{"lbs1", 21})
                        .build();
                objectOutputStream.writeObject(rpcRequest);
                objectOutputStream.flush();
                log.info("发送请求: {}", rpcRequest);

                // 读取RPC响应
                RpcResponse<User> rpcResponse = (RpcResponse<User>) objectInputStream.readObject();
                log.info("接收到响应: {}", rpcResponse);
            }
        } catch (Exception e) {
            log.error("连接服务端时发生错误: ", e);
        }
    }
}
