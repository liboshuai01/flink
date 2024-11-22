package cn.liboshuai.flink.base.proxy.handler;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
public class LogHandler implements InvocationHandler {

    /**
     * 被代理的目标对象
     */
    Object target;

    /**
     * 构造函数，接收被代理的对象
     */
    public LogHandler(Object target) {
        this.target = target;
    }

    /**
     * 实现 InvocationHandler 接口的方法，当代理对象的方法被调用时会执行此方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 方法调用前执行日志记录
        before();
        // 通过反射调用目标对象的方法，并传递参数
        Object result = method.invoke(target, args);
        // 方法调用后执行日志记录
        after();
        // 返回方法调用的结果
        return result;
    }

    /**
     * 方法调用前的日志记录
     */
    private void before() {
        log.info("执行开始时间: {}", new Date()); // 记录当前时间
    }

    /**
     * 方法调用后的日志记录
     */
    private void after() {
        log.info("执行结束时间: {}", new Date()); // 记录当前时间
    }
}
