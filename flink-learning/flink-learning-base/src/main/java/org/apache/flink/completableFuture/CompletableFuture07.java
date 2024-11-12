package org.apache.flink.completableFuture;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFuture07 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testRpc();
    }

    private static String rpc1() {
        ThreadUtils.log("模拟RPC调用：服务器server 1 开始");
        // 睡眠 400 毫秒，模拟执行耗时
        ThreadUtils.sleep(600);
        ThreadUtils.log("模拟RPC调用：服务器server 1 结束");
        return "sth. from server 1";
    }

    private static String rpc2() {
        ThreadUtils.log("模拟RPC调用：服务器server 2 开始");
        // 睡眠 1000 毫秒，模拟执行耗时
        ThreadUtils.sleep(1000);
        ThreadUtils.log("模拟RPC调用：服务器server 2 结束");
        return "sth. from server 2";
    }

    private static void testRpc() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cft1 = CompletableFuture.supplyAsync(
                CompletableFuture07::rpc1);
        CompletableFuture<String> cft2 = CompletableFuture.supplyAsync(
                CompletableFuture07::rpc2);
        CompletableFuture<String> cft3 = cft1.thenCombine(
                cft2,
                (out1, out2) -> {
                    ThreadUtils.log("合并两个服务的调用结果: {}", out1 + " & " + out2);
                    return out1 + " & " + out2;
                });
        String result = cft3.get();
        ThreadUtils.log("最终得到的结果: {}", result);
    }
}
