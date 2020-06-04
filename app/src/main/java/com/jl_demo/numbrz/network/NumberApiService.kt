package com.jl_demo.numbrz.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

class NumberApiService {

    data class NumbersResult(
        @SerializedName("text")
        val numberResponse: String,
        @SerializedName("found")
        val found: Boolean,
        @SerializedName("number")
        val number: String,
        @SerializedName("type")
        val numberType: String,
        @SerializedName("date")
        val numDate: String
    )

    interface ApiService {

        @Headers("Content-Type: application/json")
        @GET("random/math")
        fun pullRandomMathNum(): Observable<NumbersResult>


        companion object {
            private const val baseUrl = "http://numbersapi.com/"

            fun create(): ApiService {
                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
                return retrofit.create(ApiService::class.java)
            }
        }
    }


}