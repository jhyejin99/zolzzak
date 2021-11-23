package com.fromjin.zolzzak2.Util

data class ResultUser (
    val id : Number,
    val nickname: String,
    val profileImageUrl : String,
    val profileThumbnailImageUrl : String
)

data class ResultAdminNotice(
    val id: Number,
    var title: String,
    var content: String,
    var createdAt: String,
    var updatedAt: String
)