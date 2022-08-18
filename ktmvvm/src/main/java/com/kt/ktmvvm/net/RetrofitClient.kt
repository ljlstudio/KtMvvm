package com.kt.ktmvvm.net

import android.annotation.SuppressLint
import android.content.Context
import com.kt.ktmvvm.net.dns.HTTPDNSInterceptor
import com.kt.ktmvvm.net.dns.OkHttpDns
import com.kt.ktmvvm.net.event.OkHttpEventListener
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class RetrofitClient
/**
 * retrofit 初始化build
 */(var context: Context?) {


    companion object {

        @SuppressLint("StaticFieldLeak")
        private var retrofitClient: RetrofitClient? = null
        private const val DEFAULT_TIME_OUT = 15
        private val sRetrofitManager: MutableMap<Int, Retrofit> = HashMap()
        fun getInstance(context: Context?): RetrofitClient {

            if (retrofitClient == null) {
                synchronized(RetrofitClient::class.java) {
                    retrofitClient = RetrofitClient(context)
                    return retrofitClient as RetrofitClient
                }
            }
            return retrofitClient as RetrofitClient
        }
    }


    /**
     * 创建连接客户端
     */
    private fun createOkHttpClient(): OkHttpClient {

        //设置请求头拦截器
        //设置日志拦截器
        val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


        //根据需求添加不同的拦截器

        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(8, 10, TimeUnit.SECONDS)) //添加这两行代码
            .sslSocketFactory(TrustAllCerts.createSSLSocketFactory()!!, TrustAllCerts())
            .hostnameVerifier(TrustAllCerts.TrustAllHostnameVerifier())
//            .protocols(Collections.unmodifiableList(listOf(Protocol.HTTP_1_1)))
            //alibaba dns优化
//            .dns(OkHttpDns.get(context))
//            .addInterceptor(HTTPDNSInterceptor(context))
            .addInterceptor(httpLoggingInterceptor)
            .eventListenerFactory(OkHttpEventListener.FACTORY)
            .build()
    }


    /**
     * 根据host 类型判断是否需要重新创建Client，因为一个app 有不同的BaseUrl，切换BaseUrl 就需要重新创建Client
     * 所以，就根据类型来从map中取出对应的client
     */
    fun <T> getDefault(interfaceServer: Class<T>?, hostType: Int): T {
        val retrofitManager = sRetrofitManager[hostType]
        return if (retrofitManager == null) {
            create(interfaceServer, hostType)
        } else retrofitManager.create(interfaceServer!!)
    }


    /**
     *
     */
    private fun <T> create(interfaceServer: Class<T>?, hostType: Int): T {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BaseUrlConstants.getHost(hostType))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkHttpClient())
            .build()
        sRetrofitManager[hostType] = retrofit
        if (interfaceServer == null) {
            throw RuntimeException("The Api InterfaceServer is null!")
        }
        return retrofit.create(interfaceServer)
    }

}