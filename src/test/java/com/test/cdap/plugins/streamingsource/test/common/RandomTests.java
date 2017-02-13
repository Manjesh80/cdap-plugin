package com.test.cdap.plugins.streamingsource.test.common;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by cdap on 1/20/17.
 */


public class RandomTests {

    public void testKey() throws InterruptedException {

        int currentMinute = ((new DateTime()).getMinuteOfHour())%2;
        String state = (currentMinute == 0) ? "NJ" : "NY";
        System.out.printf(state);

        System.out.println((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date())); //2016/11/16 12:08:43
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
