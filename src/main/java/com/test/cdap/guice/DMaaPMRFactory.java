package com.test.cdap.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Author: mg153v (Manjesh Gowda). Creation Date: 1/25/2017.
 */
public class DMaaPMRFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DMaaPMRFactory.class);

    private final Injector injector;

    public DMaaPMRFactory(AbstractModule guiceModule) {
        injector = Guice.createInjector(guiceModule);
    }

   /* public DMaaPMRPublisher createDMaaPReciver(@Nonnull DMaaPMRPublisherConfig publisherConfig) {

    }*/
}
