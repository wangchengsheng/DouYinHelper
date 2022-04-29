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
            nodeInfo.findId("com.ss.android.ugc.aweme:id/eso").click()
                .also { if (!it) log("请回到首页") else log("开始搜索关键字") }
        }
        delay(3000)
        action {
            nodeInfo.findIdLast("com.ss.android.ugc.aweme:id/et_search_kw")
                .setText(watchVideoBean.searchKey)
        }
        action {
            nodeInfo.findId("com.ss.android.ugc.aweme:id/p=_").gestureClick()
        }
        log("搜索成功")
        action {
            val list = nodeInfo?.findAccessibilityNodeInfosByViewId("android:id/text1")
            list?.firstOrNull { it.text == "视频" }?.parent.click()
        }
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/l=a").click() }
        log("开始浏览视频")
    }

    private suspend fun seeVideo() {
        val time = Random.nextInt(15, 21)
        log("已浏览${count}个，浏览视频${time}秒")
        delay(time * 1000L)
        if (isFollow()) follow()
        if (isLike()) like()
        if (isComment()) comment()
        count++
        nextVideo()
    }

    private suspend fun follow() {
        log("关注主播")
        action { nodeInfo.findId("com.ss.android.ugc.aweme:id/user_avatar").click() }
        val followType = action {
            val no = nodeInfo.findId("com.ss.android.ugc.aweme:id/k8u")
            val yes = nodeInfo.findId("com.ss.android.ugc.aweme:id/k8v")
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
    }

    private suspend fun isAdv(): Boolean {
        val text = action {
            nodeInfo?.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/desc")
                ?.firstOrNull { it.isVisibleToUser }
        }
        return text.text.endsWith("广告")
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
