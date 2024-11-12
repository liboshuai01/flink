package org.apache.flink.util;

import java.util.regex.Matcher;

/**
 * 线程日志
 */
public class ThreadUtils {
    // 打印带有线程名称的日志
    public static void log(String message, Object... args) {
        // 获取当前线程的名称
        String threadName = Thread.currentThread().getName();

        // 使用 String.format 替换占位符
        String formattedMessage = formatMessage(message, args);

        // 打印日志
        System.out.println("[" + threadName + "] " + formattedMessage);
    }

    // 替换占位符 {}
    private static String formatMessage(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replaceFirst("\\{}", Matcher.quoteReplacement(args[i].toString()));
        }
        return message;
    }

    // 静态方法，允许调用者指定休眠时间（毫秒）
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // 将异常包装为运行时异常并抛出
            throw new RuntimeException("Thread was interrupted during sleep", e);
        }
    }
}
