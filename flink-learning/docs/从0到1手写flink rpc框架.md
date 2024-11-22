## 前言

Flink作为一个分布式流计算框架肯定涉及到了不少远程数据交换传输的情况，为了让Flink的开发人员专注于实现流计算框架本身的业务逻辑，而屏蔽远程调用其他节点方法的底层细节，那么它必然有一个通用的rpc框架实现。本篇文章意在从0到1一步步实现一个简易版的Flink Rpc框架，即使你是一个对于Flink、Rpc通信一点都不了解的小白，我也是有信心可以让你有所收获。那么下面我们开始吧！

## 前置技术

- socket
- 反射
- 动态代理
- netty
- akka

### socket

> 代码地址：[]()

Socket 是一种网络编程的基本组件，允许程序通过网络进行通信。在 Java 中，我们可以使用 ServerSocket 和 Socket 类来实现网络通信。ServerSocket 用于监听端口并接受客户端的连接，而 Socket 则用于与服务端进行通信。

下面我们将创建两个类：Server（服务端）和 Client（客户端）。服务端会监听端口，等待客户端的连接，并在接收到消息后返回响应。客户端则会连接到服务端，发送消息并接收响应。

```java
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
```

```java
package cn.liboshuai.flink.base.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class Client {
    public static void main(String[] args) throws Exception {
        // 创建Socket连接到服务端
        Socket socket = new Socket("127.0.0.1", 9991);
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

        // 关闭资源，避免内存泄漏
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
    }
}
```

## 版本一

> 代码地址：[Github](https://github.com/liboshuai01/flink/tree/learning/release-1.18/flink-learning/flink-learning-rpc/src/main/java/cn/liboshuai/flink/version1)

### 前言

下面将介绍如何通过 Java 的 Socket 和反射机制，构建一个简单的 RPC 框架的 1.0 版本。

1.0 版本的 RPC 框架由以下几个主要部分组成：

- 数据模型：定义请求和响应的数据结构。
- 服务端：处理客户端请求并返回响应。
- 客户端：发送请求并接收响应。

### 数据模型

我们定义了三个主要的类：User、RpcRequest 和 RpcResponse。

```java
package cn.liboshuai.flink.version1.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User类，用于表示用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name; // 用户名
    private int age;     // 用户年龄
    private String address; // 用户地址
}
```

```java
package cn.liboshuai.flink.version1.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RpcRequest类，用于表示RPC请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String interfaceName; // 接口名称
    private String methodName;     // 方法名称
    private Object[] params;       // 方法参数
    private Class<?>[] parameterTypes; // 参数类型
}
```

```java
package cn.liboshuai.flink.version1.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RpcResponse类，用于表示RPC响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;      // 响应状态码
    private String msg;    // 响应消息
    private T data;       // 响应数据
}
```

### 服务端实现

首先编写出客户端需要远程调用的接口`UserService`，然后使用 ServerSocket 监听客户端请求。通过反射机制，服务端能够动态调用指定的接口方法。

```java
package cn.liboshuai.flink.version1.server.service;

import cn.liboshuai.flink.version1.common.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class UserService {

    List<User> users = new ArrayList<User>();

    public UserService() {
        users.add(User.builder().name("lbs1").age(21).address("北京").build());
        users.add(User.builder().name("lbs2").age(22).address("上海").build());
        users.add(User.builder().name("lbs3").age(23).address("广州").build());
        users.add(User.builder().name("lbs4").age(24).address("深圳").build());
        users.add(User.builder().name("lbs5").age(25).address("杭州").build());
    }

    /**
     * 根据姓名和年龄查询用户信息
     */
    public User findUserByNameAndAge(String name, int age) {
        return users
                .stream()
                .filter(user -> Objects.equals(name, user.getName()) && Objects.equals(
                        age,
                        user.getAge()))
                .collect(
                        Collectors.toList()).get(0);
    }
}
```

```java
package cn.liboshuai.flink.version1.server;

import cn.liboshuai.flink.version1.common.RpcRequest;
import cn.liboshuai.flink.version1.common.RpcResponse;
import cn.liboshuai.flink.version1.common.User;
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
                        Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                        Object newInstance = aClass.getDeclaredConstructor().newInstance(); // 使用getDeclaredConstructor()以确保可以创建实例
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
```

### 客户端实现

客户端通过 Socket 连接到服务端，构造 RpcRequest 并发送。接收响应后，客户端可以直接使用返回的数据。

```java
package cn.liboshuai.flink.version1.client;

import cn.liboshuai.flink.version1.common.RpcRequest;
import cn.liboshuai.flink.version1.common.RpcResponse;
import cn.liboshuai.flink.version1.common.User;
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
                        .interfaceName("cn.liboshuai.flink.base.socket.server.service.UserService")
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
```

### 总结
通过反射机制，我们能够在运行时动态加载类和方法，这赋予了 RPC 框架更高的灵活性。服务端能够根据 RpcRequest 中的信息调用相应的方法，而无需事先硬编码。

**优点：**
- 易于理解和使用：该框架实现了基本的 RPC 功能，适合初学者学习分布式系统的基本概念，降低了入门门槛。
- 动态调用能力：通过反射，服务端可以动态调用任何符合请求的接口和方法，增强了系统的扩展性和适应性。
- 清晰的代码结构：将请求、响应和服务端逻辑分开，使代码结构更清晰，便于维护和扩展，提升了代码的可读性。

**缺点：**
- 高耦合性：网络通信、反射调用接口方法等代码与业务逻辑交织在一起，导致耦合性高，增加了代码重复率，降低了系统的可维护性。
- 扩展性不足：当前实现仅支持固定的单一接口方法进行远程 RPC 调用，缺乏灵活性，后续扩展支持多个接口方法的能力有限。
- 性能问题：使用 Socket 进行网络通信的方式为阻塞 I/O（BIO），性能较低，建议替换为更高性能的网络通信组件，如 NIO 或 Netty，以提高系统的响应速度和并发处理能力。

总体而言，这个 RPC 框架为理解远程过程调用的基本原理提供了良好的基础，但还需进一步的优化和改进，以满足更高的性能和灵活性需求。



