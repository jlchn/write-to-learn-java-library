package com.jlchn.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 *
 * it is usually used for traffic control
 *
 *
 *   private static final int THREAD_COUNT = 30;

     private static ExecutorService threadPool = Executors
     .newFixedThreadPool(THREAD_COUNT);

     private static Semaphore s = new Semaphore(10);

     public static void main(String[] args) {
     for (int i = 0; i < THREAD_COUNT; i++) {
     threadPool.execute(new Runnable() {
    @Override
    public void run() {
    try {
    s.acquire();
    System.out.println("save data");
    s.release();
    } catch (InterruptedException e) {
    }
    }
    });
     }

     threadPool.shutdown();
     }
 */
public class Semaphore {

    private static class Sync extends AbstractQueuedSynchronizer {

        public Sync(int initial) {
            this.setState(initial);
        }

        @Override
        protected int tryAcquireShared(int acquired) {
            while (true){
                int current = this.getState();
                int available = current - acquired;
                if (available < 0){
                    return available;
                }

                if (compareAndSetState(current, available)){
                    return available;
                }

            }
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            while (true){
                int current = this.getState();
                int next = current + releases;

                if (next < current){
                    throw new Error("Maximum permit count exceeded");
                }

                if (compareAndSetState(current,next)){
                    return true;
                }

            }
        }
    }

    private final Sync sync;

    public Semaphore(int initial){
        sync = new Sync(initial);
    }

    public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public void acquire(int permits) throws InterruptedException {
        if (permits < 0) throw new IllegalArgumentException();
        sync.acquireSharedInterruptibly(permits);
    }

    public void release() {
        sync.releaseShared(1);
    }

    public void release(int permits) {
        if (permits < 0) throw new IllegalArgumentException();
        sync.releaseShared(permits);
    }

}
