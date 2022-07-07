package com.kt.ktmvvm.net

class BaseUrlConstants {


    companion object {
        private const val baseUrl1: String = "http://test1/"
        private const val baseUrl2: String = "http://test2/"

        fun getHost(host: Int): String {
            when (host) {
                1 -> return baseUrl1
                2 -> return baseUrl2
            }
            return baseUrl1;
        }
    }
}