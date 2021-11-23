package com.fromjin.zolzzak2.Util

import android.icu.text.CaseMap


data class ResultSimplePost(
    val content: List<PostContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort
)

data class ResultAllPost(
    val content: List<AllPostContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort
)

data class ResultTopPost(
    val content: List<TopPostContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort
)

data class ResultDetailPost(
    val id: Number,
    val content: String,
    val address: String,
    val score: Number,
    val isOpen: Boolean,
    val countGood: Number,
    val countComment: Number,
    val createdAt: String,
    val updatedAt: String,
    val memberDto: MemberDto,
    val categoryDto: CategoryDto,
    val imageDtoList: List<ImageDto>,
    val goodDto: GoodDto

)

data class ResultNoticeList(
    val content: List<NoticeContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort
)

data class NoticeContent(
    val id: Number,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)
data class GoodDto(
    val id: Number,
    val isGood: Boolean
)

data class MemberDto(
    val id: Number,
    val nickname: String
)

data class CategoryDto(
    val id: Number,
    val name: String
)

data class PostContent(
    val content: String,
    val id: Int,
    val categoryDto: CategoryDto,
    val imageDtoList: List<ImageDto>
)
data class AllPostContent(
    val content: String,
    val address: String,
    val id: Int,
    val categoryDto: CategoryDto,
    val imageDtoList: List<ImageDto>
)

data class TopPostContent(
    val id: Int,
    val content: String,
    val countGood: Number,
    val categoryDto: CategoryDto,
    val imageDtoList: List<ImageDto>
)

data class ImageDto(
    val id: Int,
    val imageUrl: String,
    val thumbnailImageUrl: String
)

data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)