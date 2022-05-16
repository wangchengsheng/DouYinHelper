package com.hairong.douyinhelper.data

import androidx.lifecycle.MutableLiveData

class ConfigData {
    var actionDelay = 2 // 执行时间间隔
    var videoTimeStart = 10 // 浏览视频时间
    var videoTimeEnd = 18 // 浏览视频时间
    var restTimeStart = 5// 休息时间
    var restTimeEnd = 8 // 休息时间
    val showLogWindow = MutableLiveData<Boolean>() // 悬浮框显示状态
    var type = -1 // 0智能关注 1浏览视频 2取消关注
}

lateinit var configData: ConfigData

lateinit var followBean: FollowBean
lateinit var watchVideoBean: WatchVideoBean
lateinit var cancelFollowBean: CancelFollowBean