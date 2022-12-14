package com.hb.imageup

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface RetrofitPath {
    @Multipart
    @POST("/")
    fun profileSend(
        @Part imageFile : MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Call<String>

    @GET("/")//서버에 GET요청을 할 주소를 입력
    fun getTodoList() : Call<JsonObject> //MainActivity에서 사용할 json파일 가져오는 메서드
}