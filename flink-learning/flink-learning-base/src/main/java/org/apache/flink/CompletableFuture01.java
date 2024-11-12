package org.apache.flink;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFuture01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        testThenApply();
        testThenCompose();
    }

    private static void testThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> completableFuture = CompletableFuture
                .supplyAsync(new Supplier<Long>() {
                    public Long get() {
                        long firstStep = 10L + 10L;
                        // ForkJoinPool.commonPool-worker-25 - firstStep outcome is 20
                        ThreadUtils.log("firstStep outcome is {}", firstStep);
                        return firstStep;
                    }
                })
                .thenApplyAsync(new Function<Long, Long>() {
                    public Long apply(Long firstStepOutCome) {
                        long secondStep = firstStepOutCome * 2;
                        // ForkJoinPool.commonPool-worker-25 - secondStep outcome is 40
                        ThreadUtils.log("secondStep outcome is {}", secondStep);
                        return secondStep;
                    }
                });
        Long result = completableFuture.get();
        // main - final outcome is 40
        ThreadUtils.log("final outcome is {}", result);
    }

    private static void testThenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> completableFuture = CompletableFuture
                .supplyAsync(new Supplier<Long>() {
                    @Override
                    public Long get() {
                        long firstStep = 10L + 10L;
                        ThreadUtils.log("firstStep outcome is {}", firstStep);
                        return firstStep;
                    }
                })
                .thenCompose(new Function<Long, CompletionStage<Long>>() {
                    @Override
                    public CompletionStage<Long> apply(Long firstStepOutcome) {
                        // 重点：将第二个任务所要调用的普通异步方法包装成一个 CompletionStage 异步实例
                        return CompletableFuture.supplyAsync(new Supplier<Long>() {
                            // 两个任务所要调用的普通异步方法
                            @Override
                            public Long get() {
                                long secondStep = firstStepOutcome * 2;
                                ThreadUtils.log("secondStep outcome is {}", secondStep);
                                return secondStep;
                            }
                        });
                    }
                });
        Long result = completableFuture.get();
        ThreadUtils.log("final outcome is {}", result);
    }
}
