package com.suzei.minote.utils

import java.util.logging.Logger

class LogMe {

    companion object {

        fun info(message: String) {
            Logger.getLogger(LogMe::class.java.simpleName).info(message)
        }

    }

}