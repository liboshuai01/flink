package cn.liboshuai.flink;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class ConsumerSystem {
    public static void main(String[] args) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            // 创建Socket连接到服务端
            socket = new Socket("127.0.0.1", 10001);
            log.info("已连接到服务端: {}", socket.getRemoteSocketAddress());

            // 创建输出流，向服务端发送消息
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject("你好啊，我是客户端-消费者");
            objectOutputStream.flush();

            // 创建输入流，接收服务端的响应
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object object = objectInputStream.readObject();
            // 打印服务端发送的信息
            log.info("收到信息: {}", object);

        } catch (IOException | ClassNotFoundException e) {
            log.error("发生错误: {}", e.getMessage());
        } finally {
            // 关闭资源，避免内存泄漏
            try {
                if (objectOutputStream != null) objectOutputStream.close();
                if (objectInputStream != null) objectInputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                log.error("关闭资源时发生错误: {}", e.getMessage());
            }
        }
    }
}
