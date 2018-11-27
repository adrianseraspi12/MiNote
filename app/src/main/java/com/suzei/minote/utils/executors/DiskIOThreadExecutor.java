package com.suzei.minote.utils.executors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//  Executors that run on a new background thread
public class DiskIOThreadExecutor implements Executor {

    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable command) {
        mDiskIO.execute(command);
    }

}
