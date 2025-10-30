package br.com.fiap.wtcclienteapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val dummyRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val dummyApi: DummyJsonApi by lazy { dummyRetrofit.create(DummyJsonApi::class.java) }
}


