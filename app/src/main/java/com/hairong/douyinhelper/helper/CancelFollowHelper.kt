package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import com.hairong.douyinhelper.data.cancelFollowBean
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
                log("请手动打开我的关注页")
            } else {
                log("当前是关注页面")
            }
        }
    }

    /**
     * 找到需要关注的item
     */
    private suspend fun actionItem(rv: AccessibilityNodeInfo) {
        val item = action {
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
            delay(3000)
        } else {
            if (cancelFollowBean.type == 0) {
                actionCancel(item, rv)
            }
            val text = action { item.findId("com.ss.android.ugc.aweme:id/a71")?.text?.toString() }
            if (cancelFollowBean.type == 0 && text != "关注" && text != "回关") {
                actionCancel(item, rv)
            } else if (cancelFollowBean.type == 1 && text != "互相关注") {
                actionCancel(item, rv)
            } else {
                log("跳过互相关注")
                index++
                actionItem(rv)
            }
        }
    }

    private suspend fun actionCancel(item: AccessibilityNodeInfo, rv: AccessibilityNodeInfo) {
        log("正在取消")
        if (action { item.findId("com.ss.android.ugc.aweme:id/qlc").click() }) {
            if (cancelFollowBean.type == 0) {
                try {
                    // 回关取消无弹窗
                    action(5000) { nodeInfo.findId("com.ss.android.ugc.aweme:id/a6c").click() }
                } catch (e: Exception) {
                }
            }
            log("已取消")
            index++
            actionItem(rv)
        }
    }

}
