package com.calc.calc.wolframapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceProvider {
    val service: Service by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.wolframalpha.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)
    }
}