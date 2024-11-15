package org.apache.flink.akka.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: deep as the sea
 * @Site: <a href="www.doe.top">多易教育-易学堂</a>
 * @QQ: 657270652
 * @Date: 2024/2/12
 * @Tips: 学大数据，到多易教育
 * @Desc:
 *    用线程池方式测试并发性能
 **/
public class Demo4 {

    public static void main(String[] args) {

        AtomicInteger counter =  new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(70);

        System.out.println(System.currentTimeMillis() + ": 任务开始");
        for (int i = 0; i < 1000000; i++) {
            executorService.submit(new MyTask2(counter));
        }


        while(true){
            if(counter.get() == 1000000){
                System.out.println(System.currentTimeMillis() + ": 任务全部完成");
                break;
            }
        }

    }


}


class MyTask2 implements Runnable{

    AtomicInteger counter;

    public MyTask2(AtomicInteger counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        counter.incrementAndGet();
    }
}
