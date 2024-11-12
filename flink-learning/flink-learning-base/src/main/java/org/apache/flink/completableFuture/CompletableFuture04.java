package org.apache.flink.completableFuture;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;

public class CompletableFuture04 {
    public static void main(String[] args) {
        testAllOf();
    }

    private static void testAllOf() {
        CompletableFuture<Void> cft1 = CompletableFuture.runAsync(() -> ThreadUtils.log(
                "模拟异步任务1"));
        CompletableFuture<Void> cft2 = CompletableFuture.runAsync(() -> ThreadUtils.log(
                "模拟异步任务2"));
        CompletableFuture<Void> cft3 = CompletableFuture.runAsync(() -> ThreadUtils.log(
                "模拟异步任务3"));
        CompletableFuture<Void> cft4 = CompletableFuture.runAsync(() -> ThreadUtils.log(
                "模拟异步任务4"));
        CompletableFuture<Void> cftAll = CompletableFuture.allOf(
                cft1,
                cft2,
                cft3,
                cft4);
        cftAll.join();
    }
}
