package com.suzei.minote.utils;

import androidx.test.espresso.IdlingResource;

public class EspressoIdlingResource {

    private static final String RESOURCE_NAME = "GLOBAL";

    private static SimpleIdlingCountingResource sSimpleIdlingCountingResource =
            new SimpleIdlingCountingResource(RESOURCE_NAME);

    public static void increment() {
        sSimpleIdlingCountingResource.increment();
    }

    public static void decrement() {
        sSimpleIdlingCountingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return sSimpleIdlingCountingResource;
    }

}
