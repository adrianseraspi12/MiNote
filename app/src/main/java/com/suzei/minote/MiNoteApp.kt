package com.suzei.minote

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen

class MiNoteApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}