package cn.liboshuai.flink.base.socket.server;

import cn.liboshuai.flink.base.socket.common.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(10001);
        Socket socket = serverSocket.accept();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

        Class<?> aClass = Class.forName(rpcRequest.getInterfaceName());



        objectInputStream.close();
        objectOutputStream.close();
        serverSocket.close();
    }
}
