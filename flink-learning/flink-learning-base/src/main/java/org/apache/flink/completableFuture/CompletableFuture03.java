package org.apache.flink.completableFuture;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CompletableFuture03 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testThenCombine();
    }

    private static void testThenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cft1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                Integer firstStep = 10 + 10;
                ThreadUtils.log("firstStep outcome is {}", firstStep);
                return firstStep;
            }
        });
        CompletableFuture<Integer> cft2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {

            @Override
            public Integer get() {
                Integer secondStep = 10 + 10;
                // [ForkJoinPool.commonPool-worker-18] secondStep outcome is 20
                ThreadUtils.log("secondStep outcome is {}", secondStep);
                return secondStep;
            }
        });
        CompletableFuture<Integer> cft3 = cft1.thenCombine(
                cft2, new BiFunction<Integer, Integer, Integer>() {

                    @Override
                    public Integer apply(Integer integer, Integer integer2) {
                        // [ForkJoinPool.commonPool-worker-25] firstStep outcome is 20
                        ThreadUtils.log("combine outcome is {}", integer + integer2);
                        return integer + integer2;
                    }
                }
        );
        Integer result = cft3.get();
        // [ForkJoinPool.commonPool-worker-25] combine outcome is 40
        ThreadUtils.log("final result is {}", result);
    }
}
