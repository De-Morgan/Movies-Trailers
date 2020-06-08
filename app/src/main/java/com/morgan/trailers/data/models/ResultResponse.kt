package com.morgan.trailers.data.models

import com.google.gson.annotations.SerializedName


data class ResultResponse<T>(
    val page: Int?,
    @SerializedName("total_results")
    val totalResults: Int?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    val results: List<T> = emptyList()
)