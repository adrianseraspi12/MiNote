package com.suzei.minote

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MiNoteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}