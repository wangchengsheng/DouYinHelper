package com.hairong.douyinhelper.data

import androidx.lifecycle.MutableLiveData

class ConfigData {
    var actionDelay = 2000L // 执行时间间隔
    val showLogWindow = MutableLiveData<Boolean>() // 悬浮框显示状态
    var type = -1 // 0智能关注 1浏览视频
}

lateinit var configData: ConfigData

lateinit var followBean: FollowBean
lateinit var watchVideoBean: WatchVideoBean