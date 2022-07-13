package com.kt.ktmvvm.jetpack.room.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val name: String?,

    )

data class UserName(val uid: Int, val name: String?)