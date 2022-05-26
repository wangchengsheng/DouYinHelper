package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import com.hairong.douyinhelper.data.cancelFollowBean
import com.hairong.douyinhelper.util.action
import com.hairong.douyinhelper.util.click
import com.hairong.douyinhelper.util.findId
import com.hairong.douyinhelper.util.scrollForward
import kotlinx.coroutines.delay

class CancelFollowHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var index = 0

    override suspend fun execute() {
        actionItem()
    }

    private suspend fun actionItem() {
        log("开始查找item")
        val textList =
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/a-+")
                ?: return

        log("text size ${textList.size}")
        if (index > textList.size - 1) {
            log("下一页")
            nextPage()
            index = 0
            log("下一页成功，等待1秒")
            delay(1000)
            actionItem()
        } else {
            val textInfo = textList[index]
            val text = textInfo.text.toString()
            if (cancelFollowBean.type == 0 && text != "关注" && text != "回关") {
                actionCancel(textInfo.parent)
            } else if (cancelFollowBean.type == 1 && text == "已关注") {
                actionCancel(textInfo.parent)
            } else {
                log("跳过")
                index++
                actionItem()
            }
        }
    }

    private suspend fun nextPage() {
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/my9").scrollForward()
        }
    }

    private suspend fun actionCancel(item: AccessibilityNodeInfo) {
        log("正在取消")
        if (item.findId("com.ss.android.ugc.aweme:id/rc0").click()) {
            if (cancelFollowBean.type == 0) {
                delay(800)
                val dialogCancel = nodeInfo.findId("com.ss.android.ugc.aweme:id/a_m")
                if (dialogCancel?.text?.toString() == "取消关注") {
                    dialogCancel.click()
                    // 等待dialog关闭
                    delay(1000)
                }
            }
            log("已取消, 休息0.5秒")
            delay(500)
            index++
            actionItem()
        }
    }

}
