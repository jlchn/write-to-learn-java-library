package com.jlchn.concurrent;


import sun.misc.Unsafe;


public class AtomicLong implements java.io.Serializable {
    private volatile long value = 0;
    private Unsafe unsafe = UnsafeSupport.getUnsafe();
    private long offset;

    public AtomicLong()  throws IllegalAccessException, NoSuchFieldException {
        this.offset = this.unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
    }

    public void increment(){
        long before;
        do {
            before = this.unsafe.getLongVolatile(this, this.offset);
        } while (!this.unsafe.compareAndSwapLong(this, this.offset, before, before + 1));

    }

    public long get(){
        return this.value;
    }
}
