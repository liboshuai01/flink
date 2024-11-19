package cn.liboshuai.flink;


import cn.liboshuai.flink.request.RpcRequest;
import cn.liboshuai.flink.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Provider {
    public static void main(String[] args) throws Exception {
        // 创建ServerSocket，监听10002端口，等待客户端连接
        ServerSocket serverSocket = new ServerSocket(10002);
        log.info("服务端已启动，等待连接...");

        // 接受客户端的连接请求
        Socket socket = serverSocket.accept();
        log.info("客户端已连接: {}", socket.getRemoteSocketAddress());

        // 创建输入流，用于接收客户端发送的RPC请求
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject(); // 读取RPC请求
        log.info("接收到的rpc请求: {}", rpcRequest);

        // 通过反射获取请求的接口类
        Class<?> aClass = Class.forName(rpcRequest.getInterfaceName());
        // 获取请求的方法
        Method method = aClass.getMethod(
                rpcRequest.getMethodName(),
                rpcRequest.getParameterTypes()); // 获取方法对象

        // 调用方法并获取结果
        Object result = method.invoke(aClass.newInstance(), rpcRequest.getParameters());

        // 构建RPC响应对象
        RpcResponse rpcResponse = RpcResponse.builder()
                .status(RpcResponse.SUCCEED) // 设置响应状态为成功
                .data(result) // 设置响应数据为方法返回值
                .build();
        log.info("回传的rpc响应: {}", rpcResponse);

        // 创建输出流，向客户端发送RPC响应
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(rpcResponse); // 发送响应
        objectOutputStream.flush(); // 确保数据被发送

        // 关闭输入流、输出流、Socket和ServerSocket
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
        serverSocket.close();
    }
}
