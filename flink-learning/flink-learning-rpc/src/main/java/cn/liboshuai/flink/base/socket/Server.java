package cn.liboshuai.flink.base.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {
    public static void main(String[] args) throws Exception {
        // 创建一个ServerSocket，监听端口9991
        ServerSocket serverSocket = new ServerSocket(9991);
        log.info("服务端已启动，等待连接...");

        // 接受客户端的连接请求（同步阻塞等待客户端的连接）
        Socket socket = serverSocket.accept();
        log.info("客户端已连接: {}", socket.getRemoteSocketAddress());

        // 创建输入流，用于接收客户端发送的对象
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Object object = objectInputStream.readObject();
        // 打印客户端发送的信息
        log.info("收到信息: {}", object);

        // 创建输出流，向客户端发送响应
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject("你好啊，我是服务端-提供者");
        objectOutputStream.flush();

        // 关闭资源，避免内存泄漏
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
        serverSocket.close();
    }
}
