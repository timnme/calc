package com.calc.calc.wolframapi

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("queryresult")
    val queryresult: Queryresult? = null
)

data class Queryresult(
    @SerializedName("pods")
    val pods: List<PodsItem?>? = null
)

data class PodsItem(
    @SerializedName("subpods")
    val subpods: List<SubpodsItem?>? = null
)

data class SubpodsItem(
    @SerializedName("img")
    val img: Img? = null
)

data class Img(
    @SerializedName("src")
    val src: String? = null
)