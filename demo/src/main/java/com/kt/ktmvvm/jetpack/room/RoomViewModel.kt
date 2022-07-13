package com.kt.ktmvvm.jetpack.room

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.jetpack.room.db.User
import com.kt.ktmvvm.jetpack.room.db.UserDataBase
import com.kt.ktmvvm.jetpack.room.db.UserName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class RoomViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        val TAG: String = RoomViewModel::class.java.simpleName
    }

    /**
     *
     */
    fun createTable() {
        viewModelScope.launch(Dispatchers.IO) {
            UserDataBase.get(getApplication())
        }
    }


    /**
     * 创建一张表
     */
    fun insertTable() {
        viewModelScope.launch {

            val hotDog = UserDataBase.get(getApplication()).userDao().findById(4)

            hotDog?.let {
                Log.d(TAG, "已经有一条相同的数据啦")
            } ?: let {
                val mD = User(4, "热狗先生")
                UserDataBase.get(getApplication()).userDao().insertAll(mD)
                Log.d(TAG, "插入数据成功")
            }

            //先查询,如果没这条数据 就插入，有的话打印数据

        }
    }

    /**
     * 随机更新某条User数据
     */
    fun updateRandomData() {
        //先查询
        val random = (1..10).random()

        viewModelScope.launch {
            val user = UserDataBase.get(getApplication()).userDao().findById(random)
            user?.let {
                //如果不为空，则更新
                UserDataBase.get(getApplication()).userDao()
                    .updateUser(User(random, "我是更新整个user后的数据"))
                Log.d(TAG, "更新整个user数据成功")
            } ?: randomInsert(random)
        }
    }


    /**
     * 随机单独更新某个字段
     */
    fun updateSingleData() {
        //先查询
        val random = (1..10).random()
        viewModelScope.launch {
            val user = UserDataBase.get(getApplication()).userDao().findById(random)
            user?.let {
                UserDataBase.get(getApplication()).userDao()
                    .updateSingleName(UserName(random, "我是单独更新后的数据"))
                Log.d(TAG, "更新单独数据成功")
            } ?: randomInsert(random)
        }
    }


    /**
     * 删除数据
     */
    fun deleteAllData() {
        viewModelScope.launch {
            val random = (1..10).random()
            val user = UserDataBase.get(getApplication()).userDao().findById(random)
            user?.let {
                UserDataBase.get(getApplication()).userDao().delete(user)
                Log.d(TAG, "删除某条数据成功")
            } ?: randomInsert(random)
        }
    }


    /**
     * 随机插入某条数据，方便测试
     */
    private fun randomInsert(random: Int) {
        val mD = User(random, "热狗先生$random")
        UserDataBase.get(getApplication()).userDao().insertAll(mD)
        Log.d(TAG, "插入数据成功")
    }
}