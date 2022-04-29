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
            delay(2000)
            nextVideo()
        }
        if (count >= watchVideoBean.watchCount) {
            back()
            configData.showLogWindow.value = false
        }
    }

    private suspend fun seeVideo() {
        val time = Random.nextInt(15, 21)
        log("已浏览${count}个，浏览视频${time}秒")
        delay(time * 1000L)
//        if (isFollow()) follow()
        if (isLike()) like()
        if (isComment()) comment()
        count++
        nextVideo()
    }

//    private suspend fun follow() {
//        log("关注主播")
//        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/user_avatar").click() }
//        val followType = action {
//            val no = nodeInfo.findId("com.ss.android.ugc.aweme:id/k8u")
//            val yes = nodeInfo.findId("com.ss.android.ugc.aweme:id/k8v")
//            when {
//                no != null -> 1 to no
//                yes != null -> 2 to null
//                else -> null
//            }
//        }
//        if (followType.first == 1) followType.second.click()
//        log("关注完成")
//        delay(2000)
//        action { back() }
//    }

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
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/cff")
                ?.firstOrNull { it.isVisibleToUser }.click()
        }
        delay(1500)
        likeComment()
        log("开始评论")
        action {
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cc4").setText(text)
        }
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/cea").click() }
        log("评论成功")
        delay(2000)
        action { back() }
    }

    private suspend fun likeComment() {
        val nextInt = Random.nextInt(100)
        if (nextInt < 75) {
            log("点赞评论")
            val likeList = action {
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/c+i")
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
        log("准备浏览下一个视频")
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
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/pu2")
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
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/c+p")
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
