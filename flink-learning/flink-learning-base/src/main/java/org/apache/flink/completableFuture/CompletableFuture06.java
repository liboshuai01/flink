package org.apache.flink.completableFuture;

import org.apache.flink.util.ThreadUtils;

import java.util.concurrent.CompletableFuture;

public class CompletableFuture06 {
    public static void main(String[] args) {
        testDrinkTea();
    }

    private static void testDrinkTea() {
        // 任务 1：洗水壶 -> 烧开水
        CompletableFuture<Boolean> hotJob = CompletableFuture.supplyAsync(() -> {
            ThreadUtils.log("洗好水壶");
            ThreadUtils.log("烧开水");
            // 线程睡眠一段时间，代表烧水中
            ThreadUtils.sleep(5000);
            ThreadUtils.log("水烧开了");
            return true;
        });

        // 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
        CompletableFuture<Boolean> washJob = CompletableFuture.supplyAsync(() -> {
            ThreadUtils.log("洗茶杯");
            // 线程睡眠一段时间，代表清洗中
            ThreadUtils.sleep(1000);
            ThreadUtils.log("洗完了");
            return true;
        });

        // 任务 3：任务 1 和任务 2 完成后执行泡茶
        CompletableFuture<String> drinkJob = hotJob.thenCombine(washJob, (hotOk, washOk) -> {
            if (hotOk && washOk) {
                ThreadUtils.log("泡茶喝");
                ThreadUtils.sleep(1000);
                ThreadUtils.log("茶喝完");
                return "茶水真好喝";
            }
            return "没有茶";
        });

        // 等待任务3执行结果
        String result = drinkJob.join();
        ThreadUtils.log("任务执行结果：{}", result);
    }

}

