package com.musaan0129.assesment3.network

import com.musaan0129.assesment3.model.Desain
import com.musaan0129.assesment3.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://desain-api.michael-kaiser.my.id/api/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface DesainApiService {
    @GET("desain")
    suspend fun getDesain(
        @Header("Authorization") userId: String
    ): List<Desain>

    @Multipart
    @POST("desain")
    suspend fun postDesain(
        @Header("Authorization") userId: String,
        @Part("nama") nama: RequestBody,
        @Part("namaLatin") namaLatin: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("desain")
    suspend fun deleteDesain(
        @Header("Authorization") userId: String,
        @Query("id") id: String
    ): OpStatus

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") nama: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): OpStatus
}

object DesainApi {
    val service: DesainApiService by lazy {
        retrofit.create(DesainApiService::class.java)
    }
    fun getImageUrl(id: Long): String {
        return "${BASE_URL}desain/image/$id?timestamp=${System.currentTimeMillis()}"
    }
}
enum class ApiStatus { LOADING, SUCCESS, FAILED}