package com.test.cdap.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by cdap on 1/20/17.
 */


public class RandomTests {

    @Test
    public void testKey() throws InterruptedException {

        Set<Long> uniqueKeys = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            Long uniqueKey = new Long(System.currentTimeMillis());
            uniqueKeys.add(uniqueKey);
            System.out.println(uniqueKey.toString());
        }

        Assert.assertTrue(uniqueKeys.size() == 10);
    }
}
