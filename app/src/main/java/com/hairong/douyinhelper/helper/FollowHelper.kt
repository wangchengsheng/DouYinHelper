package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.followBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class FollowHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var index = 0
    private var count = 0
    private var commentList: List<String>? = null

    override suspend fun execute() {
        val rv = followRv()
        actionItem(rv)
        actionUserProfile()
        if (count >= followBean.followCount) {
            configData.showLogWindow.value = false
        }
    }

    private suspend fun followRv() = action {
        nodeInfo.findId("com.ss.android.ugc.aweme:id/l+z").also {
            if (it == null) {
                log("请手动打开指定作者粉丝页")
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
        val text = action { item.findId("com.ss.android.ugc.aweme:id/a71")?.text?.toString() }
        when {
            index >= rv.childCount - 1 -> {
                if (action { rv.scrollForward() }) {
                    index = 0
                    delay(5000)
                    actionItem(rv)
                }
            }
            text == "关注" || text == "回关" -> {
                action {
                    item.click().takeIf { it }.also { log("已关注${count}个") }
                }
            }
            text == "已关注" || text == "已请求" || item.hasText("进入他的主页")/*自己*/ -> {
                log("跳过已关注")
                index++
                actionItem(rv)
            }
        }
    }

    private suspend fun actionUserProfile() {
//        action {
//            // 针对进来不是选中作品的情况
//            nodeInfo.findId("com.ss.android.ugc.aweme:id/n4w")?.let {
//                if (it.childCount > 0) it.getChild(0).click() // 选中作品
//            }
//        }
        var tryTime = 0
        val type = action {
            val list =
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/iu6")
            val videoSize = list?.size ?: 0
            loge("size $videoSize")
            when (videoSize) {
                0 -> {
                    val lessVideo = nodeInfo.hasText("作品 0")
                    val privacy = nodeInfo.hasText("关注帐号即可查看内容和喜欢")
                    val banUser = nodeInfo.hasText("帐号已被封禁")
                    if (lessVideo || privacy || banUser) {
                        loge("需要跳过此账号 $lessVideo $privacy $banUser")
                        2 to null
                    } else {
                        tryTime++
                        if (tryTime > 6) 2 to null else null
                    }
                }
                in 3..Int.MAX_VALUE -> 1 to list?.firstOrNull()
                else -> 2 to null
            }
        }
        when (type.first) {
            1 -> {
                action { type.second.click() }
                actionVideo()
            }
            2 -> skipUser()
        }
    }

    /**
     * 关注用户
     */
    private suspend fun followUser() {
        delay(1000)
        log("关注用户")
        val follow = action { nodeInfo.findId("com.ss.android.ugc.aweme:id/k8u") }
        val text = follow.text.toString()
        if (text == "关注" || text == "回关") {
            if (action { follow.click() }) {
                index++
                count++
                val time = Random.nextInt(5, 9)
                log("已关注${count}个，休息 $time 秒")
                delay(time * 1000L)
//                if (type == 2) {
//                    try {
//                        action(2500) { nodeInfo.hasText("对方已设置为私密帐号") }
//                        action { back() }
//                    } catch (e: Exception) {
//                    }
//                }
            }
        }
        action { back() }
    }

    /**
     * 跳过用户
     */
    private suspend fun skipUser() {
        index++
        delay(Random.nextInt(5) * 1000L)
        log("跳过此用户，休息一会~")
        delay(Random.nextInt(3) * 1000L)
        action { back() }
    }

    private suspend fun actionVideo() {
        val seeTime = Random.nextInt(8, 15)
        log("开始浏览视频 $seeTime 秒")
        delay(seeTime * 1000L)
        // 点赞
        log("开始点赞")
        val node1 = action { nodeInfo.findId("com.ss.android.ugc.aweme:id/c+p") }
        if (node1.hasText("未点赞")) {
            action { node1.click() }
        }
        // 评论
        log("评论")
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/cff").click() }
        delay(2000)
        val text = if (followBean.customComment) {
            if (commentList == null) {
                commentList = followBean.commentText.split(',')
            }
            commentList!![Random.nextInt(commentList!!.size)]
        } else {
            followBean.commentList[Random.nextInt(followBean.commentList.size)]
        }
        action {
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cc4").setText(text)
        }
        delay(2000)
        action { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cea").click() }
        log("评论成功")
        action { back() }
        action { back() }
        followUser()
    }

}
