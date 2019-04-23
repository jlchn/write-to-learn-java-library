package com.jlchn.concurrent;


import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * this class is like the CountDownLatch except that it fire where there is at least one `signal`.
 */
public class SingleLatch {


    /**
     * we only need to make sure that the state is 0 when it is not signaled.
     */
    private static class Sync extends AbstractQueuedSynchronizer{



        boolean isSignalled(){
            return this.getState() != 0;
        }

        @Override
        protected int tryAcquireShared(int ignored) {

            /**
             * no cas required here because a thread can always acquire this shared lock no matter whether
             * it is the first thread to acquire.
             *
             * the number of count is set in the AbstractQueuedSynchronizer.doAcquireSharedInterruptibly()
             */
            if (isSignalled()) {
                return 1;
            }

            return -1;// check in AbstractQueuedSynchronizer.doAcquireSharedInterruptibly() why -1 returned here.
        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            this.setState(1);//we need to be sure that the state is non-zero when it has signaled.
            return true;
        }
    }

    private final Sync sync = new Sync();
    public boolean isSignalled() { return sync.isSignalled(); }
    public void signal()         { sync.releaseShared(1); }
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
}
