package cn.liboshuai.flink;


import cn.liboshuai.flink.request.RpcRequest;
import cn.liboshuai.flink.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 创建与服务端的Socket连接，连接到本地的10002端口
        Socket socket = new Socket("127.0.0.1", 10002);
        log.info("已连接到服务端: {}", socket.getRemoteSocketAddress());

        // 创建输出流，用于发送RPC请求到服务端
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("cn.liboshuai.flink.service.UserService") // 设置要调用的接口名
                .methodName("findUserByNameAndAge") // 设置要调用的方法名
                .parameterTypes(new Class<?>[]{String.class, int.class}) // 设置方法参数类型
                .parameters(new Object[]{"lbs03", 23}) // 设置方法参数值
                .build(); // 构建RPC请求对象
        log.info("发送的rpc请求: {}", rpcRequest);

        // 发送RPC请求到服务端
        objectOutputStream.writeObject(rpcRequest);
        objectOutputStream.flush(); // 确保数据被发送

        // 创建输入流，用于接收服务端的响应
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject(); // 读取服务端响应
        log.info("接收到的rpc响应: {}", rpcResponse); // 打印服务端的响应信息

        // 关闭输出流、输入流和Socket连接
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
    }
}
