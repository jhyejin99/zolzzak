package com.fromjin.zolzzak2.Util

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    // member
    // 회원 로그인
    @POST("members/login")
    fun login(
        @Header("kakaoToken") kakaoToken: String,
        @Header("fcmToken") fcmToken: String
    ): Call<ResultLogin>

    // access 토큰 재발급
    @POST("members/refresh")
    fun tokenRefresh(
        @Header("Authorization") refreshToken: String,
    ): Call<Void>

    // 닉네임 수정
    @PATCH("members/nickname")
    fun changeNickname(
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject,
    ): Call<MemberInfo>

    // 프로필사진 수정
    @Multipart
    @PATCH("members/profileimg")
    fun changeProfileImg(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part,
    ): Call<MemberInfo>

    // 프로필사진 기본값
    @PATCH("members/profileimg/default")
    fun defaultProfileImg(
        @Header("Authorization") authorization: String,
    ): Call<MemberInfo>

    // 회원 탈퇴
    @DELETE("members/delete")
    fun secessionMembers(
        @Header("Authorization") authorization: String,
    ): Call<Void>

    // 회원정보 받기
    @GET("members")
    fun getMemberInfo(
        @Header("Authorization") authorization: String,
    ): Call<MemberInfo>

    // 로그아웃
    @GET("members/logout")
    fun logout(
        @Header("Authorization") authorization: String,
    ): Call<Void>

    // 회원 알림설정 여부 조회
    @GET("members/notification/info")
    fun getNotificationInfo(
        @Header("Authorization") authorization: String,
    ): Call<ResultNotification>

    // 배경사진 수정
    @Multipart
    @PATCH("members/backgroundimg")
    fun setBackgroundImg(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part,
    ): Call<MemberInfo>

    // 배경화면 기본 이미지
    @PATCH("members/backgroundimg/default")
    fun setBackgroundDefaultImg(
        @Header("Authorization") authorization: String,
    ): Call<MemberInfo>

    // 관리자 공지사항 관련

    //관리자 공지사항 삭제
    @DELETE("admin/notification/{notificationId}")
    fun deleteAdminNotice(
        @Path("notificationId") notificationId:Number,
        @Header("authorization")authorization: String,
    ): Call<Void>

    //관리자 공지사항 수정
    @PATCH("admin/notification/{notificationId}")
    fun editAdminNotice(
        @Path("notificationId") notificationId:Number,
        @Header("authorization")authorization: String,
        @Body jsonObject: JsonObject
    ):Call<ResultAdminNotice>


    // category
    // 카테고리 리스트 조회
    @GET("categories")
    fun getCategories(
        @Header("Authorization") authorization: String,
    ): Call<List<Category>>

    // 카테고리 생성
    @POST("admin/categories")
    fun createCategory(
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject,
    ): Call<Category>

    // 카테고리 수정
    @PATCH("admin/categories/{categoryId}")
    fun editCategory(
        @Path("categoryId") categoryId: Number,
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject,
    ): Call<Category>

    // 카테고리 삭제
    @DELETE("admin/categories/{categoryId}")
    fun deleteCategory(
        @Path("categoryId") categoryId: Number,
        @Header("Authorization") authorization: String,
    ): Call<Void>

    // 게시글
    // 게시글 생성
    @Multipart
    @POST("posts/categories/{categoryId}")
    fun createPost(
        @Path("categoryId") categoryId: Number,
        @Header("Authorization") authorization: String,
        @Part("request") request: JsonObject,
        @Part image: ArrayList<MultipartBody.Part>,
    ): Call<ResultDetailPost>

    // 게시글 수정 이미지 제외
    @PATCH("posts/{postId}/categories/{categoryId}")
    fun editPostContent(
        @Path("postId") postId: Number,
        @Path("categoryId") categoryId: Number,
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject,
    ): Call<ResultDetailPost>

    // 게시글 수정 이미지만
    @Multipart
    @PATCH("posts/{postId}/images")
    fun editPostImages(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String,
        @Part image: ArrayList<MultipartBody.Part>,
    ): Call<Void>

    // 게시글 삭제
    @DELETE("posts/{postId}")
    fun deletePost(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String,
    ): Call<Void>

    // 내가 쓴 글 조회
    @GET("m/members/posts")
    fun getMyPost(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultSimplePost>

    // 전체 간단조회
    @GET("m/posts/all")
    fun getAllPost(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultAllPost>

    // 게시글 좋아요 순 간단조회
    @GET("m/posts/goods")
    fun getTopPosts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultTopPost>

    // 게시글 상세조회
    @GET("m/posts/{postId}")
    fun getDetailPost(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String,
    ): Call<ResultDetailPost>

    // 주소로 게시글 간단 조회
    @GET("m/posts")
    fun getPostByAddress(
        @Query("firstAddress") firstAddress: String,
        @Query("secondAddress") secondAddress: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultSimplePost>

    // 해시태그로 게시글 간단 조회
    @GET("m/posts/hashtag")
    fun getPostByHashtag(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("hashtag") hashtag: String,
        @Header("Authorization") authorization: String,
    ): Call<ResultSimplePost>



    // comment
    // 댓글 생성
    @POST("posts/{postId}/comments")
    fun createComment(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject
    ): Call<CommentContent>

    // 댓글 수정
    @PATCH("comments/{commentId}")
    fun editComment(
        @Path("commentId") commentId: Number,
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject
    ): Call<CommentContent>

    // 댓글 삭제
    @DELETE("comments/{commentId}")
    fun deleteComment(
        @Path("commentId") commentId: Number,
        @Header("Authorization") authorization: String,
    ): Call<Void>

    // 댓글 조회
    @GET("m/posts/{postId}/comments")
    fun getComment(
        @Path("postId") postId: Number,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultComment>


    // good
    // 좋아요 하기
    @POST("posts/{postId}/goods")
    fun like(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String,
    ): Call<ResultLike>

    // 좋아요 취소
    @DELETE("posts/goods/{goodId}")
    fun unlike(
        @Path("goodId") goodId: Number,
        @Header("Authorization") authorization: String,
    ): Call<ResultLike>


    // admin
    // 닉네임으로 회원 조회하기
    @GET("admin/members")
    fun getNicknameAdmin(
        @Query("nickname") nickname: String,
        @Header("Authorization") authorization: String,
    ): Call<ResultUser>

    // 회원 삭제
    @DELETE("admin/members/{memberId}")
    fun deleteMemberAdmin(
        @Path("memberId") memberId: Number,
        @Header("Authorization") authorization: String
    ): Call<Void>

    // 게시글 삭제
    @DELETE("admin/posts/{postId}")
    fun deletePostAdmin(
        @Path("postId") postId: Number,
        @Header("Authorization") authorization: String
    ): Call<Void>

    // 댓글 삭제
    @DELETE("admin/comments/{commentId}")
    fun deleteCommentAdmin(
        @Path("commentId") commentId: Number,
        @Header("Authorization") authorization: String
    ): Call<Void>

    //관리자 공지사항 작성
    @POST("admin/notification")
    fun createAdminNotice(
        @Header("Authorization") authorization: String,
        @Body jsonObject: JsonObject
    ): Call<ResultAdminNotice>

    // 공지사항 상세조회
    @GET("m/notifications/{notificationId}")
    fun getAdminNoticeDetail(
        @Path("notificationId") notificationId: Number,
        @Header("Authorization") authorization: String
    ): Call<ResultAdminNotice>



    // 공지사항 리스트 조회
    @GET("m/notifications")
    fun getNoticeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,

        ): Call<ResultNoticeList>

    // 졓이요, 댓글 알림 리스트 조회
    @GET("m/members/notifications")
    fun getUserNoticeList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authorization: String,
    ): Call<ResultUserNotice>

    // 공지사항 알림 여부 설정
    @PATCH("setting/adminnoti")
    fun setAdminNoti(
        @Header("Authorization") authorization: String
    ): Call<ResultSetAdminNoti>

    // 좋아요 알림 여부 설정
    @PATCH("setting/goodnoti")
    fun setGoodNoti(
        @Header("Authorization") authorization: String
    ): Call<ResultSetGoodNoti>

    // 댓글 알림 여부 설정
    @PATCH("setting/commentnoti")
    fun setCommentNoti(
        @Header("Authorization") authorization: String
    ): Call<ResultSetCommentNoti>

}