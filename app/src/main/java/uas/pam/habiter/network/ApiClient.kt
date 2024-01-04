package uas.pam.habiter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uas.pam.habiter.api.ApiService

object ApiClient {

    private val BASE_URL = "https://new-habiter-api.vercel.app"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService:ApiService = retrofit.create(ApiService::class.java)
}
