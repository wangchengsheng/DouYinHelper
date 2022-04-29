package com.hairong.douyinhelper.data

class WatchVideoBean : BaseBean() {
    var searchKey = ""
    var isFollow = false
    var follow = 30 // 关注概率
    var isLikeComment = false
    var like = 60 // 点赞概率
    var comment = 50 // 评论概率
    var watchCount = 30 // 关注数量
}