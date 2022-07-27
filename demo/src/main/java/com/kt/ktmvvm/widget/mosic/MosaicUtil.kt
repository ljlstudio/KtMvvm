package com.kt.ktmvvm.widget.mosic

import android.graphics.*

object MosaicUtil {
    enum class Effect {
        MOSAIC, BLUR
    }

    enum class MosaicType {
        MOSAIC, MosaicType, ERASER
    }

    /**
     * 马赛克效果(Native)
     *
     * @param bitmap 原图
     * @param
     * @param
     * @return 马赛克图片
     */
    fun getMosaic(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val grid = 50
        val radius = width / grid

        val mosaicBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(mosaicBitmap)
        val horCount = Math.ceil((width / radius.toFloat()).toDouble()).toInt()
        val verCount = Math.ceil((height / radius.toFloat()).toDouble()).toInt()
        val paint = Paint()
        paint.isAntiAlias = true
        for (horIndex in 0 until horCount) {
            val y = (horCount - 0.5f) * 50
            for (verIndex in 0 until verCount) {
                val x = (verIndex - 0.5f) * 50
                val l = radius * horIndex
                val t = radius * verIndex
                var r = l + radius
                if (r > width) {
                    r = width
                }
                var b = t + radius
                if (b > height) {
                    b = height
                }
                val color = bitmap.getPixel(l, t)
                val rect = Rect(l, t, r, b)
                paint.color = color
                canvas.drawRect(rect, paint)
            }
        }
        canvas.save()
        return mosaicBitmap
    }


    fun bitmapMosaic(bitmap: Bitmap?, BLOCK_SIZE: Int): Bitmap? {
        if (bitmap == null || bitmap.width == 0 || bitmap.height == 0 || bitmap.isRecycled
        ) {
            return null
        }
        val paint = Paint()
        paint.isAntiAlias = true
        val mBitmapWidth = bitmap.width
        val mBitmapHeight = bitmap.height
        val mBitmap = Bitmap.createBitmap(
            mBitmapWidth, mBitmapHeight,
            Bitmap.Config.ARGB_8888
        ) //创建画布
        val row = mBitmapWidth / BLOCK_SIZE // 获得列的切线
        val col = mBitmapHeight / BLOCK_SIZE // 获得行的切线
        val block = IntArray(BLOCK_SIZE * BLOCK_SIZE)
        for (i in 0..row) {
            val y = (row - 0.5f) * BLOCK_SIZE
            for (j in 0..col) {
                val x = (col - 0.5f) * BLOCK_SIZE
                var length = block.size
                var flag = 0 // 是否到边界标志
                if (i == row && j != col) {
                    length = (mBitmapWidth - i * BLOCK_SIZE) * BLOCK_SIZE
                    if (length == 0) {
                        break // 边界外已经没有像素
                    }
                    bitmap.getPixels(
                        block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                        BLOCK_SIZE
                    )
                    flag = 1
                } else if (i != row && j == col) {
                    length = (mBitmapHeight - j * BLOCK_SIZE) * BLOCK_SIZE
                    if (length == 0) {
                        break // 边界外已经没有像素
                    }
                    bitmap.getPixels(
                        block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                * BLOCK_SIZE, BLOCK_SIZE, mBitmapHeight - j
                                * BLOCK_SIZE
                    )
                    flag = 2
                } else if (i == row && j == col) {
                    length = ((mBitmapWidth - i * BLOCK_SIZE)
                            * (mBitmapHeight - j * BLOCK_SIZE))
                    if (length == 0) {
                        break // 边界外已经没有像素
                    }
                    bitmap.getPixels(
                        block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                        mBitmapHeight - j * BLOCK_SIZE
                    )
                    flag = 3
                } else {
                    bitmap.getPixels(
                        block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE
                    ) //取出像素数组
                }
                var r = 0
                var g = 0
                var b = 0
                var a = 0
                for (k in 0 until length) {
                    r += Color.red(block[k])
                    g += Color.green(block[k])
                    b += Color.blue(block[k])
                    a += Color.alpha(block[k])
                }
                val color = Color.argb(
                    a / length, r / length, g / length, b
                            / length
                ) //求块内所有颜色的平均值
                paint.color = color
                for (k in 0 until length) {
                    block[k] = color
                }
                when (flag) {
                    1 -> {
                        mBitmap.setPixels(
                            block, 0, mBitmapWidth - i * BLOCK_SIZE,
                            i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            BLOCK_SIZE
                        )
                    }
                    2 -> {
                        mBitmap.setPixels(
                            block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, BLOCK_SIZE, mBitmapHeight - j
                                    * BLOCK_SIZE
                        )
                    }
                    3 -> {
                        mBitmap.setPixels(
                            block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, mBitmapWidth - i * BLOCK_SIZE,
                            mBitmapHeight - j * BLOCK_SIZE
                        )
                    }
                    else -> {
                        mBitmap.setPixels(
                            block, 0, BLOCK_SIZE, i * BLOCK_SIZE, j
                                    * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE
                        )
                    }
                }
            }
        }
        return mBitmap
    }

    fun getCirleBitmap(bmp: Bitmap): Bitmap? {
        //获取bmp的宽高 小的一个做为圆的直径r
        val w = bmp.width
        val h = bmp.height
        val r = w.coerceAtMost(h)

        //创建一个paint
        val paint = Paint()
        paint.isAntiAlias = true

        //新创建一个Bitmap对象newBitmap 宽高都是r
        val newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888)

        //创建一个使用newBitmap的Canvas对象
        val canvas = Canvas(newBitmap)

        //创建一个BitmapShader对象 使用传递过来的原Bitmap对象bmp
        val bitmapShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        //paint设置shader
        paint.shader = bitmapShader

        //canvas画一个圆 使用设置了shader的paint
        canvas.drawCircle((r / 2).toFloat(), (r / 2).toFloat(), (r / 2).toFloat(), paint)
        return newBitmap
    }

    /**
     * 马赛克效果
     *
     * @param pixels
     * @param width
     * @param height
     * @param radius
     * @return
     */
    fun mosatic(pixels: IntArray, width: Int, height: Int, radius: Int): IntArray? {
        val b = radius * height / width
        val size = width * height
        val result = IntArray(size)
        for (i in 0 until width) {
            for (j in 0 until height) {
                var x = i - i % radius + radius / 2
                x = if (x > width) width else x
                var y = j - j % b + b / 2
                y = if (y > height) height else y
                val curentColor = pixels[x + y * width]
                result[i + j * width] = curentColor
            }
        }
        return result
    }

    /**
     * 模糊效果
     *
     * @param bitmap 原图像
     * @return 模糊图像
     */
    fun getBlur(bitmap: Bitmap): Bitmap? {
        val iterations = 1
        val radius = 8
        val width = bitmap.width
        val height = bitmap.height
        val inPixels = IntArray(width * height)
        val outPixels = IntArray(width * height)
        val blured = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height)
        for (i in 0 until iterations) {
            blur(inPixels, outPixels, width, height, radius)
            blur(outPixels, inPixels, height, width, radius)
        }
        blured.setPixels(inPixels, 0, width, 0, 0, width, height)
        return blured
    }

    private fun blur(
        `in`: IntArray, out: IntArray, width: Int, height: Int,
        radius: Int
    ) {
        val widthMinus = width - 1
        val tableSize = 2 * radius + 1
        val divide = IntArray(256 * tableSize)
        for (index in 0 until 256 * tableSize) {
            divide[index] = index / tableSize
        }
        var inIndex = 0
        for (y in 0 until height) {
            var outIndex = y
            var ta = 0
            var tr = 0
            var tg = 0
            var tb = 0
            for (i in -radius..radius) {
                val rgb = `in`[inIndex + clamp(i, 0, width - 1)]
                ta += rgb shr 24 and 0xff
                tr += rgb shr 16 and 0xff
                tg += rgb shr 8 and 0xff
                tb += rgb and 0xff
            }
            for (x in 0 until width) {
                out[outIndex] = (divide[ta] shl 24 or (divide[tr] shl 16)
                        or (divide[tg] shl 8) or divide[tb])
                var i1 = x + radius + 1
                if (i1 > widthMinus) i1 = widthMinus
                var i2 = x - radius
                if (i2 < 0) i2 = 0
                val rgb1 = `in`[inIndex + i1]
                val rgb2 = `in`[inIndex + i2]
                ta += (rgb1 shr 24 and 0xff) - (rgb2 shr 24 and 0xff)
                tr += (rgb1 and 0xff0000) - (rgb2 and 0xff0000) shr 16
                tg += (rgb1 and 0xff00) - (rgb2 and 0xff00) shr 8
                tb += (rgb1 and 0xff) - (rgb2 and 0xff)
                outIndex += height
            }
            inIndex += width
        }
    }

    private fun clamp(x: Int, a: Int, b: Int): Int {
        return if (x < a) a else if (x > b) b else x
    }
}