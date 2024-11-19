package cn.liboshuai.flink;

import cn.liboshuai.flink.pojo.User;
import cn.liboshuai.flink.proxy.LogHandler;
import cn.liboshuai.flink.service.UserService;
import cn.liboshuai.flink.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

// 使用 Lombok 的 @Slf4j 注解自动生成日志记录器
@Slf4j
public class ProxyDemo {
    public static void main(String[] args) {
        // 创建 UserServiceImpl 的实例
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        // 获取 UserServiceImpl 的类加载器
        ClassLoader classLoader = userServiceImpl.getClass().getClassLoader();

        // 获取 UserServiceImpl 实现的接口
        Class<?>[] interfaces = userServiceImpl.getClass().getInterfaces();

        // 创建一个日志处理器的实例
        InvocationHandler logHandler = new LogHandler(userServiceImpl);

        // 创建代理对象，代理 UserServiceImpl
        UserService proxy = (UserService) Proxy.newProxyInstance(classLoader, interfaces, logHandler);

        // 通过代理对象调用方法
        User user = proxy.findUserByNameAndAge("lbs04", 24);
        // 记录找到的用户信息
        log.info("user: {}", user);
    }
}
