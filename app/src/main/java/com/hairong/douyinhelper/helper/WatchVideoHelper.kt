package com.hairong.douyinhelper.helper

import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.watchVideoBean
import com.hairong.douyinhelper.util.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class WatchVideoHelper(service: DouYinHelperService) : BaseHelper(service) {

    private var needSearch = true
    private var commentList: List<String>? = null
    private var count = 0

    override suspend fun execute() {
        if (needSearch) {
            needSearch = false
            search()
        }
        if (!isAdv() && !hasLike()) {
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

    private suspend fun search() {
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/e73").click()
                .also { if (!it) log("请回到首页") else log("开始搜索关键字") }
        }
        delay(3000)
        action {
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/et_search_kw")
                .setText(watchVideoBean.searchKey)
        }
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/q9r").gestureClick()
        }
        log("搜索成功")
        action {
            val list = nodeInfo?.findAccessibilityNodeInfosByViewId("android:id/text1")
            list?.firstOrNull { it.text == "视频" }?.parent.click()
        }
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/m09").click() }
        log("开始浏览视频")
    }

    private suspend fun seeVideo() {
        val time = Random.nextInt(configData.videoTimeStart, configData.videoTimeEnd + 1)
        log("已浏览${count}个，浏览视频${time}秒")
        delay(time * 1000L)
        if (needFollow()) follow()
        if (isLike()) like()
        if (isComment()) comment()
        count++
        nextVideo()
    }

    private suspend fun follow() {
        log("关注主播")
//        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/user_avatar").click() }
        val followType = action {
            val no = nodeInfo.findId("com.ss.android.ugc.aweme:id/lwt")
            val yes = nodeInfo.findId("com.ss.android.ugc.aweme:id/lwu")
            when {
                no != null -> 1 to no
                yes != null -> 2 to null
                else -> null
            }
        }
        if (followType.first == 1) followType.second.click()
        log("关注完成")
        delay(2000)
        action { back() }
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
    }

    private suspend fun isAdv(): Boolean {
        val text = try {
            action(3000) {
                nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/desc")
                    ?.firstOrNull { it.isVisibleToUser }
            }
        } catch (e: Exception) {
            // 没有描述文本
            return true
        }
        return text.text.endsWith("广告")
    }

    private suspend fun hasLike(): Boolean {
        val like = action {
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/dj9")
                ?.firstOrNull { it.isVisibleToUser }
        }
        return like.contentDescription.startsWith("已点赞")
    }

    private suspend fun needFollow(): Boolean {
        var needFollow = watchVideoBean.isFollow && Random.nextInt(1, 100) <= watchVideoBean.follow
        if (needFollow) {
            try {
                action(3000) { nodeInfo.findId("com.ss.android.ugc.aweme:id/user_avatar").click() }
            } catch (e: Exception) {
                needFollow = false
                log("跳过视频")
                delay(2000)
            }
        }
        return needFollow
    }

    private fun isLike(): Boolean =
        watchVideoBean.isLikeComment && Random.nextInt(1, 100) <= watchVideoBean.like

    private fun isComment(): Boolean =
        watchVideoBean.isLikeComment && Random.nextInt(1, 100) <= watchVideoBean.comment

}
