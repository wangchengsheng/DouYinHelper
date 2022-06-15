package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import com.hairong.douyinhelper.data.cancelFollowBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay

class CancelFollowHelper(private val service: DouYinHelperService) : BaseHelper(service) {

    override suspend fun execute() {
        actionItem()
    }

    private suspend fun actionItem() {
        log("开始查找item")
        val textList =
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/a+=")
                ?.filter { it.isVisibleToUser }
                ?.filter {
                    val text = it.text.toString()
                    if (cancelFollowBean.type == 0) {
                        text == "已关注" || text == "互相关注"
                    } else {
                        text == "已关注"
                    }
                }
                ?: return

        loge("textList size=${textList.size}")
        if (textList.isEmpty()) {
            log("下一页")
            nextPage()
            log("下一页成功，等待1秒")
            delay(1000)
            actionItem()
        } else {
            actionCancel(textList.first().parent)
        }
    }

    private suspend fun nextPage() {
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/m42").scrollForward()
        }
    }

    private suspend fun actionCancel(item: AccessibilityNodeInfo) {
        log("正在取消")
        if (item.findId("com.ss.android.ugc.aweme:id/rkj").click()) {
            if (cancelFollowBean.type == 0) {
                delay(1000)
                val textList = nodeInfo?.findAccessibilityNodeInfosByText("取消关注")
                textList?.forEach {
                    if (it.isClickable) if (it.click()) {
                        // 等待dialog关闭
                        delay(2800)
                        // dialog 关闭之后获取不到界面信息，手动切换一下界面
                        if (nodeInfo.findId("com.ss.android.ugc.aweme:id/a+=") == null) {
                            if (gestureAnyClick()) {
                                delay(2000)
                                action { back() }
                            }
                        }
                        return@forEach
                    }
                }
            }
            log("已取消, 休息0.5秒")
            delay(500)
            actionItem()
        }
    }

}
