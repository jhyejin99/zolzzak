package com.fromjin.zolzzak2.Util


data class MemberInfo(
    var id: Number = 0,
    var nickname: String? = null,
    var profileImageUrl: String? = null,
    var profileThumbnailImageUrl: String? = null,
    var backGroundImageUrl: String? = null,
    var role: String? = null
)

data class ResultLogin(
    var role: String? = null
)

data class ResultNotification(
    var adminNoti : Boolean? = true,
    var goodNoti : Boolean? = true,
    var commentNoti : Boolean? = true,
)

