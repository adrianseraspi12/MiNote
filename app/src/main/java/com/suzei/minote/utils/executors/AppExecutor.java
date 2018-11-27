package com.suzei.minote.utils.executors;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final int THREAD_COUNT = 3;

    private static AppExecutor sInstance;

    private final Executor mDiskIO;

    private final Executor mMainThread;

    private final Executor mNetworkIO;

    private AppExecutor(Executor mDiskIO, Executor mMainThread, Executor mNetworkIO) {
        this.mDiskIO = mDiskIO;
        this.mMainThread = mMainThread;
        this.mNetworkIO = mNetworkIO;
    }

    public static AppExecutor getInstance() {

        if (sInstance == null) {
            sInstance = new AppExecutor(
                    new DiskIOThreadExecutor(),
                    Executors.newFixedThreadPool(THREAD_COUNT),
                    new MainThreadExecutor());
        }

        return sInstance;
    }

    public Executor getDiskIO() {
        return mDiskIO;
    }

    public Executor getMainThread() {
        return mMainThread;
    }

    public Executor getNetworkIO() {
        return mNetworkIO;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
