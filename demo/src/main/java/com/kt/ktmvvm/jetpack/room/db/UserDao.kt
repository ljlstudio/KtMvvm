package com.kt.ktmvvm.jetpack.room.db

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>?

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<User>?

    @Query("SELECT * FROM user WHERE name LIKE :name")
    suspend fun findByName(name: String): User?

    @Query("SELECT *FROM user WHERE uid LIKE:userId")
    suspend fun findById(userId: Int): User?

    @Update(entity = User::class)
    suspend fun updateSingleName(vararg name: UserName?)

    @Update
    suspend fun updateUser(vararg user: User)

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}