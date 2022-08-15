package com.kt.ktmvvm.entity

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.kt.ktmvvm.jetpack.camerax.CameraViewModel
import java.io.Serializable

class AlbumData : Serializable {


    companion object {
        val ALBUM_ID_ALL = "-1"
        val ALBUM_NAME_ALL = "All"

        private var mId: String? = null
        private var mCoverPath: Uri? = null
        private var mDisplayName: String? = null
        private var mCount: Long = 0

        @SuppressLint("Range")
        fun valueOf(cursor: Cursor): AlbumData {
            val index = cursor.getColumnIndex(CameraViewModel.COLUMN_URI)
            val count = cursor.getColumnIndex(CameraViewModel.COLUMN_COUNT)
            val uri = if (index >= 0) cursor.getString(index) else null
            return AlbumData(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                Uri.parse(uri ?: ""),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                if (count > 0) cursor.getLong(count) else 0
            )
        }

    }


    constructor(id: String?, coverUri: Uri?, displayName: String?, count: Long) {
        mId = id
        mCoverPath = coverUri
        mDisplayName = displayName
        mCount = count
    }

    constructor(source: Parcel) {
        mId = source.readString()
        mCoverPath = source.readParcelable(Uri::class.java.classLoader)
        mDisplayName = source.readString()
        mCount = source.readLong()
    }


    fun getId(): String? {
        return mId
    }

    /**
     * 获取封面路径
     * @return
     */
    fun getCoverUri(): Uri? {
        return mCoverPath
    }

    /**
     * 获取显示名称
     * @return
     */
    fun getDisplayName(): String? {
        return if (isAll()) {
            "所有照片"
        } else mDisplayName
    }

    /**
     * 获取相册数量
     * @return
     */
    fun getCount(): Long {
        return mCount
    }

    /**
     * 加入拍摄item
     */
    fun addCaptureCount() {
        mCount++
    }

    fun isAll(): Boolean {
        return ALBUM_ID_ALL == mId
    }

    fun isEmpty(): Boolean {
        return mCount == 0L
    }

    override fun toString(): String {
        return "AlbumData{" +
                "mId='" + mId + '\'' +
                ", mCoverPath='" + mCoverPath + '\'' +
                ", mDisplayName='" + mDisplayName + '\'' +
                ", mCount=" + mCount +
                '}'
    }
}