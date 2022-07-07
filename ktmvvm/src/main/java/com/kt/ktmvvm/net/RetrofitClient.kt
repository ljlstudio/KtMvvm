package com.kt.ktmvvm.net

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {

    /**
     * retrofit 初始化build
     */
    private fun RetrofitClient() {}


    companion object {
        private var retrofitClient: RetrofitClient? = null
        private const val DEFAULT_TIME_OUT = 15
        private val sRetrofitManager: MutableMap<Int, Retrofit> = HashMap()
        fun getInstance(): RetrofitClient {
            if (retrofitClient == null) {
                synchronized(RetrofitClient::class.java) {
                    retrofitClient = RetrofitClient()
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
            .addInterceptor(httpLoggingInterceptor)


            //                .addInterceptor(chain -> {
            ////                    /**这个回调每次网络请求都会走,适合经常变化的公参*/
            ////                    Request request = chain.request();
            ////                    Request.Builder requestBuilder = request.newBuilder();
            ////                    HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
            ////                    String sourceChannel = HeadParams.getSourceChannel();
            ////                    if (!request.url().toString().contains("device/reportInfo")) {
            ////                        httpUrlBuilder.addQueryParameter("imei", HeadParams.getImei());
            ////                        httpUrlBuilder.addQueryParameter("oaid", HeadParams.getOaid());
            ////                        httpUrlBuilder.addQueryParameter("androidId", HeadParams.getAndroidId());
            ////                        httpUrlBuilder.addQueryParameter("Channel",
            ////                                (TextUtils.isEmpty(sourceChannel) || "-1".equals(sourceChannel)) ? HeadParams.getChannelId() : sourceChannel);
            ////
            ////                    }
            ////                    httpUrlBuilder.addQueryParameter("imsi", HeadParams.getImsi());
            ////                    httpUrlBuilder.addQueryParameter("FirstLinkTime", HeadParams.getFirstLinkTime());
            ////                    httpUrlBuilder.addQueryParameter("SecondLinkTime", HeadParams.getClientFirstLinkTime());
            ////                    httpUrlBuilder.addQueryParameter("wifi", HeadParams.getWifi());
            ////                    httpUrlBuilder.addQueryParameter("regID", HeadParams.getRegId());
            ////                    httpUrlBuilder.addQueryParameter("userTag",  PrefsUtil.getInstance().getString(Constants.CLEAN_UMENG_TAG_KEY, ""));
            //
            //                    Request lastRequest = requestBuilder.url(httpUrlBuilder.build()).build();
            //                    /* 如果需要qabr3接口的话.你懂得 */
            //
            //                    return chain.proceed(lastRequest);
            //                })
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