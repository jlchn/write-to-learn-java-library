package com.jlchn.concurrent;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = -5179523762034025856L;

    private final Sync sync;

    public ReentrantLock(){
        this.sync = new NonFairSync();
    }

    public ReentrantLock(boolean fair){
        this.sync = fair? new FairSync() : new NonFairSync();
    }

    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -517958862034025860L;

        void lock(){
            acquire(1);
        }
        void unlock(){
            release(1);
        }
        protected abstract boolean tryAcquire(int acquires);

        protected final boolean tryRelease(int releases) {

            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            int c = getState() - releases;
            if (c == 0) { // release the lock only when count == 0
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }
        private void readObject(java.io.ObjectInputStream s)
                throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    private class NonFairSync extends Sync {
        protected boolean tryAcquire(int acquires){

            int count = this.getState();

            if (count == 0 && compareAndSetState(0, 1)){
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }else if(this.getExclusiveOwnerThread() == Thread.currentThread()) {
                int next = count + acquires;
                if (next < 0){
                    throw new  Error("Overflow: Maximum lock count exceeded.");
                }
                setState(next);// no cas required because the current thread is holding the lock.
                return true;

            }

            return false;
        }
    }

    private class FairSync extends Sync{
        protected boolean tryAcquire(int acquires){

            int count = this.getState();

            /**
             * first blocked thread should get the lock first
             * hasQueuedPredecessors to tell if there are predecessors,
             * this is the key to make it Fair
             */
            if (count == 0 && !hasQueuedPredecessors() && compareAndSetState(0, 1)){
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }else if(this.getExclusiveOwnerThread() == Thread.currentThread()) {
                int next = count + acquires;
                if (next < 0) {
                    throw new  Error("Overflow: Maximum lock count exceeded.");
                }
                setState(next);// no cas required because the current thread is holding the lock.
                return true;

            }

            return false;
        }
    }

    @Override
    public void lock() {
        sync.lock();
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
        sync.unlock();
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }


}
