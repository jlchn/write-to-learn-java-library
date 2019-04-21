package com.jlchn.concurrent;


import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeSupport {
    public static Unsafe getUnsafe(){
        Unsafe unsafe = null;
        try {
            Field f =Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe =  (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return unsafe;
    }
}
