package uas.pam.habiter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import uas.pam.habiter.api.ApiService

object ApiClient {

    private const val BASE_URL = "https://habiter-api.vercel.app"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService:ApiService = retrofit.create(ApiService::class.java)
}
