package com.suzei.minote.utils.executors

import java.util.concurrent.Executor
import java.util.concurrent.Executors

//  Executors that run on a new background thread
class DiskIOThreadExecutor : Executor {

    private val mDiskIO: Executor

    init {
        mDiskIO = Executors.newSingleThreadExecutor()
    }

    override fun execute(command: Runnable) {
        mDiskIO.execute(command)
    }

}
