package com.jlchn.concurrent;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;


/**
 * there should be an initial state( > 0)
 *
 * countdown the state when signal
 *
 * no need to update state in the await
 */
public class CountDownLatch {

    private static class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int arg) {

            /**
             * initial value should be greater than 0
             * if the state equals 0, then it means the await method has returned.
             */
            if (this.getState() == 0) {
                return 1;
            }

            return -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {

            if (this.getState() < 0){
                throw new IllegalMonitorStateException();
            }

            while (true){
                int current = this.getState();

                if (current == 0){
                    return false;
                }

                if (!compareAndSetState(current, current -1)){
                    continue;
                }

                return (current - 1) != 0;
            }

        }
    }

    private final Sync sync;
    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        sync.releaseShared(1);
    }


}
