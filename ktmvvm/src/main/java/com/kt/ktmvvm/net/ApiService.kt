package com.kt.ktmvvm.net

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface ApiService {


    /**
     * get
     */
    @GET(ApiAddress.LOGIN)
    suspend fun login(
        @Query("account") account: String,
        @Query("password") password: String
    ): BaseResponse<Any>


    /**
     * post body
     */
    @POST(ApiAddress.LOGIN)
    suspend fun loginBody(@Body requestBody: RequestBody): BaseResponse<Any>
}