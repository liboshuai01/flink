package cn.liboshuai.flink.client.util;

import cn.liboshuai.flink.common.rpc.RpcRequest;
import cn.liboshuai.flink.common.rpc.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class IOUtils {
    public static RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket("127.0.0.1", 10002)) {
            log.info("已连接到服务端: {}", socket.getRemoteSocketAddress());

            // 创建输入输出流
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                objectOutputStream.writeObject(rpcRequest);
                objectOutputStream.flush();
                log.info("发送请求: {}", rpcRequest);

                // 读取RPC响应
                RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
                log.info("接收到响应: {}", rpcResponse);
                return rpcResponse;
            }
        } catch (Exception e) {
            log.error("连接服务端时发生错误: ", e);
            return null;
        }
    }
}
