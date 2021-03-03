package com.suzei.minote.utils.executors

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor

class AppExecutor @VisibleForTesting
internal constructor(val diskIO: Executor, val mainThread: Executor) {

    private class MainThreadExecutor : Executor {

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

    companion object {

        val instance: AppExecutor

            get() {

                return AppExecutor(
                        DiskIOThreadExecutor(),
                        MainThreadExecutor())
            }
    }

}
