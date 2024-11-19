package cn.liboshuai.flink;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class ConsumerSystem {
    public static void main(String[] args) throws Exception {
        // 创建Socket连接到服务端
        Socket socket = new Socket("127.0.0.1", 10001);
        log.info("已连接到服务端: {}", socket.getRemoteSocketAddress());

        // 创建输出流，向服务端发送消息
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject("你好啊，我是客户端-消费者");
        objectOutputStream.flush();

        // 创建输入流，接收服务端的响应
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Object object = objectInputStream.readObject();
        // 打印服务端发送的信息
        log.info("收到信息: {}", object);

        // 关闭资源
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
    }
}
