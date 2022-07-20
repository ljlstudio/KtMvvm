package com.kt.ktmvvm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import com.google.gson.Gson
import java.io.*
import java.util.*

@SuppressLint("StaticFieldLeak")
object PrefsUtil {


    var prefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    var mContext: Context? = null
    //    private static PrefsUtil prefsUtil;
//    public synchronized static PrefsUtil getInstance() {
//        return prefsUtil;
//    }

    //    private static PrefsUtil prefsUtil;
    //    public synchronized static PrefsUtil getInstance() {
    //        return prefsUtil;
    //    }
    private var instance: PrefsUtil? = null

     fun getInstance(): PrefsUtil? {
        if (instance == null) {
            synchronized(PrefsUtil::class.java) {
                if (instance == null) {
                    instance = PrefsUtil
                }
            }
        }
        return instance
    }


    fun init(context: Context?, prefsname: String?, mode: Int) {
        instance = getInstance()
        instance?.mContext = context
        instance?.prefs = instance?.mContext?.getSharedPreferences(prefsname, mode)
        instance?.editor = instance?.prefs?.edit()
    }


    fun getBoolean(key: String?, defaultVal: Boolean): Boolean {
        return prefs?.getBoolean(key, defaultVal) == false
    }

    fun getBoolean(key: String?): Boolean {
        return prefs?.getBoolean(key, false) == false
    }

    fun getString(key: String?, defaultVal: String?): String? {
        return prefs?.getString(key, defaultVal)
    }

    fun getString(key: String?): String? {
        return prefs?.getString(key, null)
    }

    fun getInt(key: String?, defaultVal: Int): Int? {
        return prefs?.getInt(key, defaultVal)
    }

    fun getInt(key: String?): Int? {
        return prefs?.getInt(key, 0)
    }

    fun getFloat(key: String?, defaultVal: Float): Float? {
        return prefs?.getFloat(key, defaultVal)
    }

    fun getFloat(key: String?): Float? {
        return prefs?.getFloat(key, 0f)
    }

    fun getLong(key: String?, defaultVal: Long): Long? {
        return prefs?.getLong(key, defaultVal)
    }

    fun getLong(key: String?): Long? {
        return prefs?.getLong(key, 0L)
    }


    fun putString(key: String?, value: String?): PrefsUtil {
        editor?.putString(key, value)
        editor?.apply()
        return this
    }

    fun applyString(key: String?, value: String?): PrefsUtil {
        editor?.putString(key, value)
        editor?.apply()
        return this
    }

    fun putInt(key: String?, value: Int): PrefsUtil {
        editor?.putInt(key, value)
        editor?.apply()
        return this
    }

    fun applyInt(key: String?, value: Int): PrefsUtil {
        editor?.putInt(key, value)
        editor?.apply()
        return this
    }

    fun putFloat(key: String?, value: Float): PrefsUtil {
        editor?.putFloat(key, value)
        editor?.apply()
        return this
    }

    fun putLong(key: String?, value: Long): PrefsUtil {
        editor?.putLong(key, value)
        editor?.apply()
        return this
    }

    fun applyLong(key: String?, value: Long): PrefsUtil {
        editor?.putLong(key, value)
        editor?.apply()
        return this
    }

    fun putBoolean(key: String?, value: Boolean): PrefsUtil {
        editor?.putBoolean(key, value)
        editor?.apply()
        return this
    }

    fun applyBoolean(key: String?, value: Boolean): PrefsUtil {
        editor?.putBoolean(key, value)
        editor?.apply()
        return this
    }

    fun putStringSet(key: String?, value: Set<String?>?): PrefsUtil {
        editor?.putStringSet(key, value)
        editor?.apply()
        return this
    }

    fun removeKey(key: String?) {
        editor?.remove(key)
        editor?.apply()
    }

    fun getStringSet(key: String?): Set<String?>? {
        return prefs?.getStringSet(key, null)
    }


    fun putObject(key: String?, `object`: Any?) {
        if (`object` == null) {
            editor?.putString(key, null)
        }


        editor?.putString(key, Gson().toJson(`object`))
        editor?.apply()
    }

    fun <T> getObject(key: String, a: Class<T>?): T? {
        val json = prefs?.getString(key, null)
        if (TextUtils.isEmpty(json)) {
            return null
        } else {
            try {


                return Gson().fromJson(json, a)
            } catch (e: Exception) {

            }
        }
        return null
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    fun getListString(key: String?): ArrayList<String>? {
        return ArrayList(
            Arrays.asList(
                *TextUtils.split(
                    prefs?.getString(key, ""), "‚‗‚"
                )
            )
        )
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    fun putListString(key: String?, stringList: ArrayList<String>) {
        checkForNullKey(key)
        val myStringList = stringList.toTypedArray()
        editor?.putString(key, TextUtils.join("‚‗‚", myStringList))?.apply()
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     *
     * @param key the pref key
     */
    fun checkForNullKey(key: String?) {
        if (key == null) {

        }
    }

    /**
     * 存储Map集合
     *
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
    </V></K> */
    fun <K : Serializable?, V : Serializable?> putMap(key: String, map: Map<K, V>?): PrefsUtil{
        try {
            put(key, map)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun <K : Serializable?, V : Serializable?> getMap(key: String): MutableMap<K, V>? {
        try {
            return get(key) as MutableMap<K, V>?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 存储对象
     */
    @Throws(IOException::class)
    private fun put(key: String, obj: Any?) {
        if (obj == null) { //判断对象是否为空
            return
        }
        val baos = ByteArrayOutputStream()
        var oos: ObjectOutputStream? = null
        oos = ObjectOutputStream(baos)
        oos.writeObject(obj)
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        val objectStr = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
        baos.close()
        oos.close()
        putString(key, objectStr)
    }

    /**
     * 获取对象
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private operator fun get(key: String): Any? {
        val wordBase64 = getString(key, "")
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null
        }
        val objBytes = Base64.decode(wordBase64?.toByteArray(), Base64.DEFAULT)
        val bais = ByteArrayInputStream(objBytes)
        val ois = ObjectInputStream(bais)
        // 将byte数组转换成product对象
        val obj = ois.readObject()
        bais.close()
        ois.close()
        return obj
    }
}