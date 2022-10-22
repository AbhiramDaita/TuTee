package com.jpnce.tutee


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    fun getInstance(): Retrofit {
        val baseUrl = "https://sheets.googleapis.com"
        println(baseUrl)
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }
}
