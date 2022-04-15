package com.hairong.douyinhelper

import android.app.Application
import android.content.Context
import com.hairong.douyinhelper.data.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        configData = ConfigData()
        followBean = FollowBean()
        watchVideoBean = WatchVideoBean()
    }
}

lateinit var appContext: Context