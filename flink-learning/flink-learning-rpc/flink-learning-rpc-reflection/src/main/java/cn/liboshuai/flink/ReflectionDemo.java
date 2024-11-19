package cn.liboshuai.flink;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射demo
 */
@Slf4j
public class ReflectionDemo {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 对象全类名
        String interfaceName = "cn.liboshuai.flink.service.UserService";
        // 方法名
        String methodName = "findUserByNameAndAge";
        // 参数列表
        Object[] parameters = new Object[]{"lbs02", 22};
        // 参数类型
        Class<?>[] parameterTypes = new Class<?>[]{String.class, int.class};

        // 根据'对象全类名'获取class对象
        Class<?> aClass = Class.forName(interfaceName);
        // 根据class对象创建该类的实例对象
        Object newInstance = aClass.newInstance();
        // 根据方法名称与参数类型获取 Method 对象
        Method method = aClass.getMethod(methodName, parameterTypes);
        // Method 根据实例对象与参数列表进行实际的方法调用，得到最终结果
        Object result = method.invoke(newInstance, parameters);

        // 结果：User(name=lbs02, age=22, address=北京, phone=100100100102)
        log.info("结果：{}", result);
    }
}
