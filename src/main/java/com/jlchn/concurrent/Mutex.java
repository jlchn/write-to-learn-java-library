package com.jlchn.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;



public class Mutex implements Lock, Serializable {


    private final Sync sync = new Sync();

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/AbstractQueuedSynchronizer.html
     */

    private static class Sync extends AbstractQueuedSynchronizer{

        @Override
        protected boolean isHeldExclusively() {
            return this.getState() == 1;
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            while (compareAndSetState(0,1)){
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            if (this.getState() != 1){
                throw new IllegalMonitorStateException();
            }

            this.setState(0);
            this.setExclusiveOwnerThread(null);
            return true;
        }

        Condition newCondition(){
            return new ConditionObject();
        }

        // Deserialize properly
        private void readObject(ObjectInputStream s)
                throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }



    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.tryRelease(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
