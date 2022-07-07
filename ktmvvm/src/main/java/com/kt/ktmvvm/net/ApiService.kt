package com.kt.ktmvvm.net

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface ApiService {


    @GET(ApiAddress.LOGIN)
    suspend fun login(
        @Query("account") account: String,
        @Query("password") password: String
    ): BaseResponse<Any>


}