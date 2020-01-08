package com.calc.calc.wolframapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
    @GET("/v2/query?")
    fun getChart(
        @Query("input", encoded = true) input: String,
        @Query("format") format: String = "image",
        @Query("output") output: String = "JSON",
        @Query("appid") appid: String = "YOUR-APP-ID"
    ): Call<ApiResponse>
}