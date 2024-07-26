package com.jawnpaul.shopapp.utils

import com.google.common.io.Resources
import com.thejawnpaul.core.remote.ProductService
import java.io.File
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val okHttpClient: OkHttpClient
    get() = OkHttpClient.Builder()
        .build()

internal fun getJson(path: String): String {
    val uri: URL = Resources.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
}

internal fun makeTestApiService(mockWebServer: MockWebServer): ProductService = Retrofit.Builder()
    .baseUrl(mockWebServer.url("/"))
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    .create(ProductService::class.java)
