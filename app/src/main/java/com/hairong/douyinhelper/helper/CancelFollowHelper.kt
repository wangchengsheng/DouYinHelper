package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.followBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay

class CancelFollowHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var index = 0

    override suspend fun execute() {
        val rv = followRv()
        actionItem(rv)
    }

    private suspend fun followRv() = action {
        nodeInfo.findId("com.ss.android.ugc.aweme:id/l+z").also {
            if (it == null) {
                log("请手动打开粉丝页")
            } else {
                log("当前是粉丝页面")
            }
        }
    }

    /**
     * 找到需要关注的item
     */
    private suspend fun actionItem(rv: AccessibilityNodeInfo) {
        val item = action(1200000) {
            if (rv.childCount <= 0) {
                log("列表为空，正在刷新")
                rv.refresh()
                null
            } else {
                rv.getChild(index)
            }
        }
        if (index >= rv.childCount - 1 && action { rv.scrollForward() }) {
            index = 0
            delay(5000)
            actionItem(rv)
        }
        when (action { item.findId("com.ss.android.ugc.aweme:id/a71")?.text?.toString() }) {
            "已关注", "已请求" -> {
                if (action { item.findId("com.ss.android.ugc.aweme:id/qlc").click() }) {
                    index++
                    actionItem(rv)
                }
            }
            else -> {
                log("跳过互相关注")
                index++
                actionItem(rv)
            }
        }
    }

}
