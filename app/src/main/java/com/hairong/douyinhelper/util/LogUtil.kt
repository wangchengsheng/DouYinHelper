package com.hairong.douyinhelper.util

import android.util.Log
import com.hairong.douyinhelper.BuildConfig

fun loge(msg: String?, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.e("zwonb", msg, t)
    }
}