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
        actionItem()
        actionUserProfile()
        if (count >= followBean.followCount) {
            configData.showLogWindow.value = false
        }
    }

    private suspend fun actionItem() {
        log("开始查找item")
        val textList = action {
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/a-+")
        }
        log("text size ${textList.size}")
        if (index > textList.size - 1) {
            log("下一页")
            nextPage()
            index = 0
            log("下一页成功，等待2秒")
            delay(2000)
            actionItem()
        } else {
            val textInfo = textList[index]
            val text = textInfo.text.toString()
            when (text) {
                "关注", "回关" -> {
                    textInfo.parent.parent.click()
                    log("已关注${count}个")
                }
                else -> {
                    log("跳过已关注")
                    index++
                    actionItem()
                }
            }
        }
    }

    private suspend fun nextPage() {
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/my9").scrollForward()
        }
    }

    private suspend fun actionUserProfile() {
        var tryTime = 0
        val type = action {
            val list =
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/i+l")
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
        val follow = action { nodeInfo.findId("com.ss.android.ugc.aweme:id/lrd") }
        val text = follow.text.toString()
        if (text == "关注" || text == "回关") {
            if (action { follow.click() }) {
                index++
                count++
                val time = Random.nextInt(configData.restTimeStart, configData.restTimeEnd + 1)
                log("已关注${count}个，休息 $time 秒")
                delay(time * 1000L)
            }
        }
        action { back() }
    }

    /**
     * 跳过用户
     */
    private suspend fun skipUser() {
        index++
        val time = Random.nextInt(configData.restTimeStart, configData.restTimeEnd + 1)
        log("跳过此用户，休息$time 秒")
        delay(time * 1000L)
        action { back() }
    }

    private suspend fun actionVideo() {
        val seeTime = Random.nextInt(configData.videoTimeStart, configData.videoTimeEnd + 1)
        log("开始浏览视频 $seeTime 秒")
        delay(seeTime * 1000L)
        // 点赞
        log("开始点赞")
        val node1 = action { nodeInfo.findId("com.ss.android.ugc.aweme:id/did") }
        if (node1.hasText("未点赞")) {
            action { node1.click() }
        }
        // 评论
        log("评论")
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/cmt").click() }
        delay(2000)
        val text = if (followBean.customComment) {
            if (commentList == null) {
                commentList = followBean.commentText.split(',')
            }
            commentList!![Random.nextInt(commentList!!.size)]
        } else {
            followBean.commentList[Random.nextInt(followBean.commentList.size)]
        }
        try {
            action(5000) { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cj5").setText(text) }
            log("设置评论内容成功")
        } catch (e: Exception) {
            // 首评弹窗会设置失败
            log("设置评论内容失败")
            back()
            back()
            delay(1500)
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cj5").setText(text)
        }
        try {
            action(3000) { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cli").click() }
        } catch (e: Exception) {
            log("点击发送失败")
        }
        log("评论成功")
        action { back() }
        action { back() }
        followUser()
    }

}
