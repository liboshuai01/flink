package cn.liboshuai.flink;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class ProviderSystem {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            // 创建一个ServerSocket，监听端口10001
            serverSocket = new ServerSocket(10001);
            log.info("服务端已启动，等待连接...");

            // 接受客户端的连接请求（同步阻塞等待客户端的连接）
            socket = serverSocket.accept();
            log.info("客户端已连接: {}", socket.getRemoteSocketAddress());

            // 创建输入流，用于接收客户端发送的对象
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object object = objectInputStream.readObject();
            // 打印客户端发送的信息
            log.info("收到信息: {}", object);

            // 创建输出流，向客户端发送响应
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject("你好啊，我是服务端-提供者");
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            log.error("发生错误: {}", e.getMessage());
        } finally {
            // 关闭资源，避免内存泄漏
            try {
                if (objectOutputStream != null) objectOutputStream.close();
                if (objectInputStream != null) objectInputStream.close();
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                log.error("关闭资源时发生错误: {}", e.getMessage());
            }
        }
    }
}
