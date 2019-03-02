package com.suzei.minote.utils.executors

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutor @VisibleForTesting
internal constructor(val diskIO: Executor, val mainThread: Executor, val networkIO: Executor) {

    private class MainThreadExecutor : Executor {

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

    companion object {

        private val THREAD_COUNT = 3

        val instance: AppExecutor

            get() {

                return AppExecutor(
                        DiskIOThreadExecutor(),
                        MainThreadExecutor(),
                        Executors.newFixedThreadPool(THREAD_COUNT))
            }
    }

}
