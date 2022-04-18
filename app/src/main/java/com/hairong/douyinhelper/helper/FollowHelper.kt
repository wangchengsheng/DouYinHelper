package com.hairong.douyinhelper.helper

import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.followBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay

class FollowHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var index = 0
    private var count = 0

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
        val item = action {
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
                    actionItem(rv)
                }
            }
            text == "关注" -> {
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
        val type = action {
            val noVideo = nodeInfo.hasText("作品 0")
            val privacy = nodeInfo.hasText("关注帐号即可查看内容和喜欢")
            val banUser = nodeInfo.hasText("帐号已被封禁")
            val hasVideo = nodeInfo.findId("com.ss.android.ugc.aweme:id/iu6")
            when {
                hasVideo != null -> 1 to hasVideo
                noVideo -> 2 to null
                privacy -> 3 to null
                banUser -> 4 to null
                else -> null
            }
        }
        when (type.first) {
            1 -> {
                action { type.second.click().also { if (it) log("开始浏览视频一小会") } }
                actionVideo()
            }
            2 -> {
                log("作品 0")
                if (followBean.skipVideo0) skipUser() else followUser(type.first)
            }
            3 -> {
                log("私密账号")
                if (followBean.skipPrivacy) skipUser() else followUser(type.first)
            }
            4 -> {
                log("帐号已被封禁")
                skipUser()
            }
        }
    }

    /**
     * 关注用户
     * [type] 3私密账号第一次关注后会弹窗
     */
    private suspend fun followUser(type: Int) {
        delay(1000)
        log("关注用户")
        val follow = action { nodeInfo.findId("com.ss.android.ugc.aweme:id/k8u") }
        if (follow.text.toString() == "关注") {
            if (action { follow.click() }) {
                index++
                count++
                log("已关注${count}个，休息一会~")
                delay(5000)
                if (type == 3) {
                    try {
                        action(3000) { nodeInfo.hasText("对方已设置为私密帐号") }
                        action { back() }
                    } catch (e: Exception) {
                    }
                }
            }
        }
        action { back() }
    }

    /**
     * 跳过用户
     */
    private suspend fun skipUser() {
        index++
        delay(3000)
        log("跳过此用户，休息一会~")
        delay(3000)
        action { back() }
    }

    private suspend fun actionVideo() {
        delay(8000)
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
        action {
            val edit = nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cc4")
            val bundle =
                bundleOf(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE to followBean.comment)
            edit?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle) ?: false
        }
        delay(2000)
        action { nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/cea").click() }
        log("评论成功")
        action { back() }
        action { back() }
        followUser(1)
    }

}
