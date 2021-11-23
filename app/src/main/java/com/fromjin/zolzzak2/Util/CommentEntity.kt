package com.fromjin.zolzzak2.Util

data class ResultComment(
    val content: List<CommentContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort
)


data class CommentContent(
    val id: Number,
    val content: String,
    val memberDto: MemberDto
)