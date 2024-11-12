package org.apache.flink.completableFuture;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFuture05 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testApplyToEither();
    }

    public static void testApplyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> ctf1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                Integer firstStep = 10 + 10;
                ThreadUtils.sleep(1000);
                ThreadUtils.log("firstStep outcome is {}", firstStep);
                return firstStep;
            }
        });
        CompletableFuture<Integer> cft2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                Integer secondStep = 100 + 100;
                ThreadUtils.sleep(2000);
                ThreadUtils.log("secondStep outcome is {}", secondStep);
                return secondStep;
            }
        });

        CompletableFuture<Integer> cft3 = ctf1.applyToEither(
                cft2,
                new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer thirdStep) {
                        ThreadUtils.log("thirdStep outcome is {}", thirdStep);
                        return thirdStep;
                    }
                });
        Integer finalResult = cft3.get();
        ThreadUtils.log("final result is {}", finalResult);
    }
}
