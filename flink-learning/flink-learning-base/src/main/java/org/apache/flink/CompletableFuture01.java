package org.apache.flink;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFuture01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testThenApply();
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
}
