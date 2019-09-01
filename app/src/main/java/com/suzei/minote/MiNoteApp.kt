package com.suzei.minote

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen

class MiNoteApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        MobileAds.initialize(this)
    }

}