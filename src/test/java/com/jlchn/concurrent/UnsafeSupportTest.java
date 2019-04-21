package com.jlchn.concurrent;

import org.junit.Assert;
import org.junit.Test;
import sun.misc.Unsafe;

public class UnsafeSupportTest {


    @Test
    public void test(){
        Unsafe unsafe = UnsafeSupport.getUnsafe();

        Assert.assertNotNull(unsafe);
    }

}
