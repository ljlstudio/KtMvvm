package com.kt.ktmvvm.net.dns


import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.HttpDnsService
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

/**
 * DNS 优化
 */
class OkHttpDns(context: Context?) : Dns {
    private val SYSTEM = Dns.SYSTEM
    private var httpDns: HttpDnsService? = null

    init {
        httpDns = HttpDns.getService(context)
    }


    companion object {
        private var instance: OkHttpDns? = null
        fun get(context: Context?): OkHttpDns {
            if (instance == null) {
                synchronized(OkHttpDns::class.java) {
                    if (instance == null) {
                        instance = OkHttpDns(context)
                    }
                }
            }
            return instance!!
        }
    }

    override fun lookup(hostname: String): MutableList<InetAddress> {
        //通过异步解析接⼝获取ip
        val ip = httpDns?.getIpByHostAsync(hostname)
        ip?.let {

            val inetAddresses = listOf(InetAddress.getAllByName(ip)) as MutableList<InetAddress>
            Log.e("OkHttpDns", "inetAddresses:$inetAddresses")
            return inetAddresses
        } ?: let {
            return Dns.SYSTEM.lookup(hostname)
        }
    }


}