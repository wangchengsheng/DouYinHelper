package com.hairong.douyinhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.ui.CancelFollowPage
import com.hairong.douyinhelper.ui.FollowPage
import com.hairong.douyinhelper.ui.MainPage
import com.hairong.douyinhelper.ui.WatchVideoPage
import com.hairong.douyinhelper.ui.theme.DouYinHelperTheme

class MainActivity : ComponentActivity() {

    private var showType by mutableStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DouYinHelperTheme {
                MainPage {
                    showType = it
                    configData.type = it
                }
                AnimatedVisibility(visible = showType == 0, enter = fadeIn(), exit = fadeOut()) {
                    FollowPage { showType = -1 }
                }
                AnimatedVisibility(visible = showType == 1, enter = fadeIn(), exit = fadeOut()) {
                    WatchVideoPage { showType = -1 }
                }
                AnimatedVisibility(visible = showType == 2, enter = fadeIn(), exit = fadeOut()) {
                    CancelFollowPage { showType = -1 }
                }
            }
        }
    }

}