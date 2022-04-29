package com.hairong.douyinhelper.data

open class BaseBean {
    var customComment = true
    var commentText = "不错哦,支持" // 评论内容多个逗号隔开
    val commentList by lazy(LazyThreadSafetyMode.NONE) {
        arrayOf(
            "今天多一份拼搏，明天多几份欢笑",
            "你不怕困难，困难就怕你",
            "有期望在的地方，痛苦也成欢乐",
            "给你点赞，再附送一朵花花❀",
            "点赞点赞点赞，重要的事情说三遍！",
            "太棒了，赞一个!",
            "ღ( ´･ᴗ･` )比心",
            "6666，⁶⁶⁶⁶⁶卧槽⁶⁶⁶⁶⁶666 ⁶⁶⁶⁶⁶卧槽⁶⁶⁶⁶⁶666⁶⁶⁶⁶⁶卧槽⁶⁶⁶⁶⁶66⁶⁶⁶⁶卧槽⁶⁶⁶⁶666牛逼",
            "牛逼，不得不说，有被震惊到",
            "牛逼，book思议啊",
            "忍不住想捂脸\uD83E\uDD26",
            "明明可以靠颜值，偏偏要靠才华！",
            "奖励你一朵小红花",
        )
    }
}