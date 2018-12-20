package com.suzei.minote.utils;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.test.espresso.IdlingResource;

public class SimpleIdlingCountingResource implements IdlingResource {

    private String resourceName;

    private ResourceCallback resourceCallback;

    private final AtomicInteger counter = new AtomicInteger(0);

    SimpleIdlingCountingResource(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        return resourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void increment() {
        counter.getAndIncrement();
    }

    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0) {
            //  We are now in Idle
            if (resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0) {
            throw new IllegalArgumentException("Invalid! Counter is below 0, take action now!");
        }
    }

}
