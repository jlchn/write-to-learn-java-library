package com.jlchn.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangli
 * @date 21/04/2019
 */
public class AtomicLongTest {

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        AtomicLong counter = new AtomicLong();
        ExecutorService executor = Executors.newFixedThreadPool(1000);

        long start = System.currentTimeMillis();
        for (int i = 0 ; i < 1000; i++){
            executor.submit(new CounterThread(counter));
        }

        /**
         * The shutdown() method doesn’t cause an immediate destruction of the ExecutorService.
         * It will make the ExecutorService stop accepting new tasks and shut down after all
         * running threads finish their current work.
         */
        executor.shutdown();
        /**
         * Blocks until all tasks have completed execution after a shutdown
         * request, or the timeout occurs, or the current thread is interrupted
         */
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long stop = System.currentTimeMillis();

        Assert.assertEquals(100000000L, counter.get());
        System.out.println(stop - start);
    }
}

class CounterThread implements Runnable {

    private AtomicLong counter;

    public CounterThread(AtomicLong counter){
        this.counter = counter;
    }
    @Override
    public void run() {

        for (int i = 0; i < 100000; i ++){
            this.counter.increment();
        }
    }
}
