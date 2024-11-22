package cn.liboshuai.flink.server;

import cn.liboshuai.flink.common.RpcRequest;
import cn.liboshuai.flink.common.RpcResponse;
import cn.liboshuai.flink.common.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {
    private static final int PORT = 10001; // 服务器端口

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("服务端已启动，等待连接...");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    log.info("客户端已连接: {}", socket.getRemoteSocketAddress());

                    // 创建输入输出流
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                         ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

                        // 读取RPC请求
                        RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
                        log.info("接收到请求: {}", rpcRequest);

                        // 通过反射调用方法
                        Class<?> aClass = Class.forName(rpcRequest.getInterfaceName());
                        Method method = aClass.getMethod(
                                rpcRequest.getMethodName(),
                                rpcRequest.getParameterTypes());
                        Object newInstance = aClass
                                .getDeclaredConstructor()
                                .newInstance(); // 使用getDeclaredConstructor()以确保可以创建实例
                        User user = (User) method.invoke(newInstance, rpcRequest.getParams());

                        // 创建并发送RPC响应
                        RpcResponse<User> rpcResponse = new RpcResponse<>();
                        rpcResponse.setCode(200);
                        rpcResponse.setMsg("请求成功");
                        rpcResponse.setData(user);
                        objectOutputStream.writeObject(rpcResponse);
                        objectOutputStream.flush();
                    }
                } catch (Exception e) {
                    log.error("处理客户端请求时发生错误: ", e);
                }
            }
        } catch (IOException e) {
            log.error("服务器启动失败: ", e);
        }
    }
}
