package com.hairong.douyinhelper.helper

import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.watchVideoBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class SameCityHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var commentList: List<String>? = null
    private var count = 0
    private val keyWork by lazy(LazyThreadSafetyMode.NONE) { watchVideoBean.searchKey.split(",") }

    override suspend fun execute() {
        if (!isAdvNoKeyWord() && !hasLike()) {
            seeVideo()
        } else {
            log("跳过视频")
            delay(1000)
            nextVideo()
        }
        if (count >= watchVideoBean.watchCount) {
            back()
            configData.showLogWindow.value = false
        }
    }

    private suspend fun seeVideo() {
        val time = Random.nextInt(configData.videoTimeStart, configData.videoTimeEnd + 1)
        log("已浏览${count}个，浏览视频${time}秒")
        delay(time * 1000L)
        if (isLike()) like()
        if (isComment()) comment()
        count++
        nextVideo()
    }

    private suspend fun like() {
        log("点赞视频")
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/viewpager").gestureDoubleClick() }
        log("点赞成功")
        delay(2000)
    }

    private suspend fun comment() {
        log("评论视频")
        val text = if (watchVideoBean.customComment) {
            if (commentList == null) {
                commentList = watchVideoBean.commentText.split(',')
            }
            commentList!![Random.nextInt(commentList!!.size)]
        } else {
            watchVideoBean.commentList[Random.nextInt(watchVideoBean.commentList.size)]
        }
        action {
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/cn6")
                ?.firstOrNull { it.isVisibleToUser }.click()
        }
        delay(1500)
        try {
            likeComment()
        } catch (e: Exception) {
            loge("评论内容找不到")
        }
        log("开始评论")
        try {
            action(5000) { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/ckz").setText(text) }
            log("设置评论内容成功")
        } catch (e: Exception) {
            // 首评弹窗会设置失败
            log("设置评论内容失败")
            back()
            back()
            delay(1500)
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/ckz").setText(text)
        }
        try {
            action(3000) { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cml").click() }
        } catch (e: Exception) {
            log("点击发送失败")
        }
        log("评论成功")
        delay(2000)
        action { back() }
    }

    private suspend fun likeComment() {
        val nextInt = Random.nextInt(100)
        if (nextInt < 75) {
            log("点赞评论")
            val likeList = action(8000) {
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/dj1")
            }
            if (likeList.isNotEmpty()) {
                action { likeList.firstOrNull().click() }
                if (likeList.size > 1) {
                    action { likeList[1].click() }
                }
            }
        }
    }

    private suspend fun nextVideo() {
        val time = Random.nextInt(configData.restTimeStart, configData.restTimeEnd + 1)
        log("休息$time 秒")
        delay(time * 1000L)
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/viewpager").gestureForward()
        }
        log("开始下一个视频")
    }

    private suspend fun isAdvNoKeyWord(): Boolean {
        var tryTime = 0
        val skip = action {
            val des =
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/desc")
                    ?.firstOrNull { it.isVisibleToUser }
            val live =
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/qss")
                    ?.firstOrNull { it.isVisibleToUser }
            if (des != null) {
                val text = des.text.toString()
                when {
                    text.endsWith("广告") -> {
                        loge("广告")
                        0 to null
                    }
                    keyWork.any { text.contains(it) } -> {
                        loge("包含关键字")
                        1 to null
                    }
                    else -> {
                        0 to null
                    }
                }
            } else if (live != null && live.text.contains("直播间")) {
                loge("直播间")
                0 to null
            } else {
                loge("啥也不是") // 描述为空
                log("检测不到，准备跳过")
                tryTime++
                if (tryTime > 2) 0 to null else null
            }
        }
        return skip.first == 0
    }

    private suspend fun hasLike(): Boolean {
        val like = action {
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/dj9")
                ?.firstOrNull { it.isVisibleToUser }
        }
        return like.contentDescription.startsWith("已点赞")
    }

    private fun isFollow(): Boolean =
        watchVideoBean.isFollow && Random.nextInt(1, 100) <= watchVideoBean.follow

    private fun isLike(): Boolean =
        watchVideoBean.isLikeComment && Random.nextInt(1, 100) <= watchVideoBean.like

    private fun isComment(): Boolean =
        watchVideoBean.isLikeComment && Random.nextInt(1, 100) <= watchVideoBean.comment

}
