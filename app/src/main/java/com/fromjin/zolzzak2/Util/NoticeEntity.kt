package com.fromjin.zolzzak2.Util


data class ResultUserNotice(
    var content: List<UserNoticeContent>,
    var pageable: Pageable,
    var number: Number,
    var numberOfElements: Number,
    var first: Boolean,
    var last: Boolean,
    var size: Number,
    var sort: Sort,
    var empty: Boolean
)

data class UserNoticeContent(
    var id : Int,
    var title: String,
    var content : String,
    var postId: Int,
    var createdAt: String
)

data class ResultSetAdminNoti(
    var adminNoti: Boolean
)
data class ResultSetGoodNoti(
    var goodNoti: Boolean
)

data class ResultSetCommentNoti(
    var commentNoti: Boolean
)