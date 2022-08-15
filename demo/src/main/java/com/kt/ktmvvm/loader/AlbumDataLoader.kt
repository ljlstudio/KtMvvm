package com.kt.ktmvvm.loader

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.kt.ktmvvm.entity.AlbumData

class AlbumDataLoader : CursorLoader {

    companion object {
        val COLUMN_BUCKET_ID = "bucket_id"
        val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
        val COLUMN_URI = "uri"
        val COLUMN_COUNT = "count"
        private val QUERY_URI = MediaStore.Files.getContentUri("external")

        private val COLUMNS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_URI,
            COLUMN_COUNT
        )

        private val PROJECTION_Q = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE
        )

        private val SELECTION_ALL_Q = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private val SELECTION_ALL_ARGS = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        private val SELECTION_FOR_SINGLE_MEDIA_TYPE_Q =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
            return arrayOf(mediaType.toString())
        }

        private val NORMAL_ORDER_BY = "datetaken DESC"

        // 优先排序Camera目录
        private val BUCKET_ORDER_BY =
            "CASE bucket_display_name WHEN 'Camera' THEN 1 ELSE 100 END ASC, datetaken DESC"



        /**
         * 获取图片加载器，相册不做排序
         *
         * @param context
         * @return
         */
        fun getImageLoaderWithoutBucketSort(context: Context): CursorLoader {
            return AlbumDataLoader(
                context,
                SELECTION_FOR_SINGLE_MEDIA_TYPE_Q,
                getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                NORMAL_ORDER_BY
            )
        }

        fun getImageLoader(context: Context): CursorLoader {
            return AlbumDataLoader(
                context, SELECTION_FOR_SINGLE_MEDIA_TYPE_Q,
                getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            )
        }

        fun getVideoLoader(context: Context): CursorLoader {
            return AlbumDataLoader(
                context, SELECTION_FOR_SINGLE_MEDIA_TYPE_Q,
                getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            )
        }

        fun getAllLoader(context: Context): CursorLoader {
            return AlbumDataLoader(context, SELECTION_ALL_Q, SELECTION_ALL_ARGS)
        }
    }


    constructor(context: Context, selection: String, selectionArgs: Array<String>) : super(
        context,
        QUERY_URI,
        PROJECTION_Q,
        selection,
        selectionArgs,
        BUCKET_ORDER_BY
    ) {

    }

    constructor(
        context: Context,
        selection: String,
        selectionArgs: Array<String>,
        order: String
    ) : super(context, QUERY_URI, PROJECTION_Q, selection, selectionArgs, order) {

    }


    @SuppressLint("Range")
    override fun loadInBackground(): Cursor? {
        var allAlbum: MatrixCursor? = null
        var supportAlbums: MatrixCursor? = null
        try {
            val albums = super.loadInBackground()
            allAlbum = MatrixCursor(COLUMNS)
            var totalCount = 0
            var allAlbumCoverUri: Uri? = null

            // Pseudo GROUP BY
            @SuppressLint("UseSparseArrays") val countMap: MutableMap<Long, Long> = HashMap()
            if (albums != null) {
                while (albums.moveToNext()) {
                    val bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID))
                    var count = countMap[bucketId]
                    if (count == null) {
                        count = 1L
                    } else {
                        count++
                    }
                    countMap[bucketId] = count
                }
            }

            // 支持的Cursor
            supportAlbums = MatrixCursor(COLUMNS)
            if (albums != null) {
                if (albums.moveToFirst()) {
                    allAlbumCoverUri = getUri(albums)
                    val done: MutableSet<Long> = HashSet()
                    do {
                        val bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID))
                        if (done.contains(bucketId)) {
                            continue
                        }
                        val fileId = albums.getLong(
                            albums.getColumnIndex(MediaStore.Files.FileColumns._ID)
                        )
                        val bucketDisplayName = albums.getString(
                            albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME)
                        )
                        val mimeType = albums.getString(
                            albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                        )
                        val uri = getUri(albums)
                        val lCount = countMap[bucketId]
                        val count = lCount ?: 0
                        if (count > 1) {
                            supportAlbums.addRow(
                                arrayOf(
                                    java.lang.Long.toString(fileId),
                                    java.lang.Long.toString(bucketId),
                                    bucketDisplayName,
                                    mimeType,
                                    uri.toString(), count.toString()
                                )
                            )
                            done.add(bucketId)
                            totalCount += count.toInt()
                        }
                    } while (albums.moveToNext())
                }
            }

            // 所有照片
            allAlbum.addRow(
                arrayOf(AlbumData.ALBUM_ID_ALL, AlbumData.ALBUM_ID_ALL, AlbumData.ALBUM_NAME_ALL, null,
                    allAlbumCoverUri?.toString(), totalCount.toString()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (allAlbum != null && supportAlbums != null) {
            MergeCursor(arrayOf<Cursor>(allAlbum, supportAlbums))
        } else {
            null
        }
    }

    @SuppressLint("Range")
    private fun getUri(cursor: Cursor): Uri {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
        val mimeType = cursor.getString(
            cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
        )
        val contentUri: Uri = when {
            MimeType.isImage(mimeType) -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            MimeType.isVideo(mimeType) -> {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            else -> {
                // unknown
                MediaStore.Files.getContentUri("external")
            }
        }
        return ContentUris.withAppendedId(contentUri, id)
    }
}