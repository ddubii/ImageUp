package com.hb.imageup

import android.content.Intent
import android.widget.EditText
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitSetting{

    //내꺼
    //val API_BASE_URL = "http://172.20.6.244:8080/"

    //유라언니
    val API_BASE_URL = "http://172.20.16.60:8080/"

    val httpClient = OkHttpClient.Builder()

    val baseBuilder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())

    fun <S> createBaseService(serviceClass: Class<S>?): S {
        val retrofit = baseBuilder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}

