package com.kt.ktmvvm.widget.mosic

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.sqrt as sqrt1

class DrawMosaicView : ViewGroup {


    private var mMosaicType: MosaicUtil.MosaicType = MosaicUtil.MosaicType.MOSAIC

    /**
     * 绘画板宽度
     */
    private var mImageWidth = 0

    /**
     * 绘画板高度
     */
    private var mImageHeight = 0

    /**
     * 绘画底层图片资源
     */
    private var bmBaseLayer: Bitmap? = null

    /**
     * 橡皮擦图层
     */
    private var bmCoverLayer: Bitmap? = null

    private var bmMosaicLayer: Bitmap? = null

    /**
     * 画笔
     */
    private var mBrushWidth = 0

    private var mImageRect: Rect? = null

    private var mPaint: Paint? = null

    private var mPadding = 0

    /**
     * 触摸路径数据
     */
    private var touchPaths: MutableList<MosaicPath>? = null
    private var tempPaths: MutableList<MosaicPath>? = null
    private var erasePaths: MutableList<MosaicPath>? = null
    private var listener: OnMosaicStateListener? = null
    private var touchPath: MosaicPath? = null


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initDrawView()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {

        initDrawView()
    }

    companion object {
        const val TAG = "MosaicView"

        // default image inner padding, in dip pixels
        private const val INNER_PADDING = 0

        /**
         * 马赛克粗细 可以按 5，10，15，20,30
         */
        private const val PATH_WIDTH = 30

    }

    /**
     * 初始化绘画板 默认的情况下是马赛克模式
     */
    private fun initDrawView() {
        touchPaths = ArrayList()
        erasePaths = ArrayList()
        tempPaths = ArrayList()
        mPadding = dp2px(INNER_PADDING)
        mBrushWidth = dp2px(PATH_WIDTH)
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeWidth = 6f
        mPaint?.color = -0xd5a356
        mImageRect = Rect()
        setWillNotDraw(false)
        setMosaicType(MosaicUtil.MosaicType.MOSAIC)
    }

    /**
     * 设置画刷的宽度
     *
     * @param brushWidth 画刷宽度大小
     */
    fun setMosaicBrushWidth(brushWidth: Int) {
        mBrushWidth = dp2px(brushWidth)
    }

    /**
     * 设置马赛克类型
     *
     * @param type 类型
     */
    fun setMosaicType(type: MosaicUtil.MosaicType) {
        this.mMosaicType = type
    }

    /**
     * 设置所要打码的图片资源
     *
     * @param imgPath 图片路径
     */
    fun setMosaicBackgroundResource(imgPath: String) {
        val file = File(imgPath)
        if (!file.exists()) {
            Log.w(TAG, "setSrcPath invalid file path $imgPath")
            return
        }
        reset(true)
        val bitmap = BitmapFactory.decodeFile(imgPath)
        mImageWidth = bitmap.width
        mImageHeight = bitmap.height
        bmBaseLayer = bitmap
        requestLayout()
        invalidate()
    }

    /**
     * 设置马赛克样式资源
     *
     * @param imgPath 样式图片路径
     */
    fun setMosaicResource(imgPath: String) {
        val file = File(imgPath)
        if (!file.exists()) {
            Log.w(TAG, "setSrcPath invalid file path $imgPath")
            setMosaicType(MosaicUtil.MosaicType.ERASER)
            return
        }
        val bitmap = BitmapFactory.decodeFile(imgPath)
        bmCoverLayer = bitmap?.let {
            setMosaicType(MosaicUtil.MosaicType.MOSAIC)
            bmCoverLayer?.recycle()
            it
        }

        updatePathMosaic()
        invalidate()
    }

    /**
     * 设置所要打码的资源图片
     *
     * @param bitmap 资源图片路径
     */
    fun setMosaicBackgroundResource(bitmap: Bitmap?, recyclerBg: Boolean) {
        if (bitmap == null) {
            Log.e(TAG, "setMosaicBackgroundResource : bitmap == null")
            return
        }
        reset(recyclerBg)
        mImageWidth = bitmap.width
        mImageHeight = bitmap.height
        bmBaseLayer = bitmap
        requestLayout()
        invalidate()
    }

    /**
     * 设置马赛克样式资源
     *
     * @param bitmap 样式图片资源
     */
    fun setMosaicResource(bitmap: Bitmap) {
        setMosaicType(MosaicUtil.MosaicType.MOSAIC)
        bmCoverLayer?.recycle()
        erasePaths?.clear()
        touchPaths?.clear()
        tempPaths?.clear()
        setTouchMosaicAmount()
        bmCoverLayer = getBitmap(bitmap)
        updatePathMosaic()
        invalidate()
    }

    private fun getBitmap(bit: Bitmap): Bitmap {
        val bitmap = Bitmap.createBitmap(
            mImageWidth, mImageHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bit, 0f, 0f, null)
        canvas.save()
        return bitmap
    }


    /**
     * 清除绘画数据
     */
    fun clear() {
        tempPaths?.clear()
        touchPaths?.clear()
        erasePaths?.clear()
        setTouchMosaicAmount()
        bmMosaicLayer?.recycle()
        bmMosaicLayer = null
        invalidate()
    }

    /**
     * 开始标记，Path
     */
    fun startTag() {
        tempPaths?.clear()
    }

    /**
     * 结束标记
     */
    fun endTag() {

        if (touchPaths?.size!! >= tempPaths?.size!!) {
            touchPaths?.removeAll(tempPaths!!)
        }
        setTouchMosaicAmount()
        updatePathMosaic()
        invalidate()
    }

    /**
     * 设置当前马赛克数量
     */
    private fun setTouchMosaicAmount() {
        listener?.mosaicAmount(touchPaths?.size)
    }


    fun undoMosaic() {
        if (touchPaths == null || touchPaths!!.size <= 0) {
            return
        }
        touchPaths?.removeAt(touchPaths?.size!! - 1)
        setTouchMosaicAmount()
        updatePathMosaic()
        invalidate()
    }

    /**
     * 重置绘画版
     *
     * @return
     */
    private fun reset(recyclerBg: Boolean): Boolean {
        mImageWidth = 0
        mImageHeight = 0

        if (recyclerBg) {
            bmCoverLayer?.recycle()
            bmCoverLayer = null
            bmBaseLayer?.recycle()
            bmBaseLayer = null
            bmMosaicLayer?.recycle()
            bmMosaicLayer = null
        }
        tempPaths?.clear()
        tempPaths?.clear()
        touchPaths?.clear()
        erasePaths?.clear()
        setTouchMosaicAmount()
        return true
    }


    /**
     * 销毁
     */
    fun destroy() {

        if (bmCoverLayer?.isRecycled == false) {
            bmCoverLayer?.recycle()
            bmCoverLayer = null
        }
        if (bmBaseLayer?.isRecycled == false) {
            bmBaseLayer?.recycle()
            bmBaseLayer = null
        }
        if (bmMosaicLayer?.isRecycled == false) {
            bmMosaicLayer?.recycle()
            bmMosaicLayer = null
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        val action = event.action
        val x = event.x.toInt()
        val y = event.y.toInt()
        onPathEvent(action, x, y)
        return true
    }

    private fun onPathEvent(action: Int, xP: Int, yP: Int) {
        var x = xP
        var y = yP
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return
        }

        if (x < mImageRect?.left!! || x > mImageRect?.right!! || y < mImageRect?.top!! || y > mImageRect?.bottom!!) {
            return
        }
        val ratio = ((mImageRect!!.right - mImageRect!!.left)
                / mImageWidth.toFloat())
        x = ((x - mImageRect?.left!!) / ratio).toInt()
        y = ((y - mImageRect?.top!!) / ratio).toInt()
        if (action == MotionEvent.ACTION_DOWN) {
            touchPath = MosaicPath()
            touchPath?.drawPath = Path()
            touchPath?.drawPath?.moveTo(x.toFloat(), y.toFloat())
            touchPath?.paintWidth = mBrushWidth
            Log.d(TAG, "the mBushWidth is$mBrushWidth")
            if (this.mMosaicType === MosaicUtil.MosaicType.MOSAIC) {
                touchPaths?.add(touchPath!!)
                tempPaths?.add(touchPath!!)
                setTouchMosaicAmount()
            } else {
                erasePaths?.add(touchPath!!)
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            touchPath!!.drawPath!!.lineTo(x.toFloat(), y.toFloat())
            updatePathMosaic()
            invalidate()
        }


    }


    //画六边形
    private fun drawPicture(path: MosaicPath) {
        val left = ((mImageWidth - sqrt1(3.0) * 10) / 2.0).toFloat()
        val top = ((mImageHeight - 2 * 10) / 2.0).toFloat()
        path.drawPath!!.moveTo((left + sqrt1(3.0) * 10 / 2.0).toFloat(), top)
        path.drawPath!!.lineTo(left, top + 10 / 2)
        path.drawPath!!.lineTo(left, top + 1.5f * 10)
        path.drawPath!!.lineTo((left + sqrt1(3.0) * 10 / 2.0f).toFloat(), top + 2 * 10)
        path.drawPath!!.lineTo((left + sqrt1(3.0) * 10).toFloat(), top + 1.5f * 10)
        path.drawPath!!.lineTo((left + sqrt1(3.0) * 10).toFloat(), top + 10 / 2.0f)
        path.drawPath!!.lineTo((left + sqrt1(3.0) * 10 / 2.0).toFloat(), top)
        path.drawPath!!.close()
    }


    /**
     * 刷新绘画板
     */
    private fun updatePathMosaic() {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return
        }
        bmMosaicLayer?.recycle()
        bmMosaicLayer = Bitmap.createBitmap(
            mImageWidth, mImageHeight,
            Bitmap.Config.ARGB_8888
        )
        val bmTouchLayer = Bitmap.createBitmap(
            mImageWidth, mImageHeight,
            Bitmap.Config.ARGB_8888
        )
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.pathEffect = CornerPathEffect(10f)
        paint.strokeWidth = mBrushWidth.toFloat()
        paint.color = Color.BLUE
        val canvas = Canvas(bmTouchLayer)
        for (path in touchPaths!!) {
            val pathTemp = path.drawPath
            val drawWidth = path.paintWidth
            paint.strokeWidth = drawWidth.toFloat()
            canvas.drawPath(pathTemp!!, paint)
        }
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        for (path in erasePaths!!) {
            val pathTemp = path.drawPath
            val drawWidth = path.paintWidth
            paint.strokeWidth = drawWidth.toFloat()
            canvas.drawPath(pathTemp!!, paint)
        }
        canvas.setBitmap(bmMosaicLayer)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawBitmap(bmCoverLayer!!, 0f, 0f, null)
        paint.reset()
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(bmTouchLayer, 0f, 0f, paint)
        paint.xfermode = null
        canvas.save()
        bmTouchLayer.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bmBaseLayer?.isRecycled == false) {
            canvas.drawBitmap(bmBaseLayer!!, null, mImageRect!!, null)
        }
        if (bmMosaicLayer?.isRecycled == false) {
            canvas.drawBitmap(bmMosaicLayer!!, null, mImageRect!!, null)
        }
    }

    override fun onLayout(
        changed: Boolean, left: Int, top: Int, right: Int,
        bottom: Int
    ) {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return
        }
        val contentWidth = right - left
        val contentHeight = bottom - top
        val viewWidth = contentWidth - mPadding * 2
        val viewHeight = contentHeight - mPadding * 2
        val widthRatio = viewWidth / mImageWidth.toFloat()
        val heightRatio = viewHeight / mImageHeight.toFloat()
        Log.e(TAG, "the viewWidth is$viewWidth")
        Log.e(TAG, "the viewHeight is$viewHeight")
        Log.e(TAG, "the mImageWidth is$mImageWidth")
        Log.e(TAG, "the mImageHeight is$mImageHeight")

        val ratio = if (widthRatio < heightRatio) widthRatio else heightRatio
        Log.e(TAG, "the ratio is$ratio")
        val realWidth = (mImageWidth * ratio).toInt()
        val realHeight = (mImageHeight * ratio).toInt()
        val imageLeft = (contentWidth - realWidth) / 2
        val imageTop = (contentHeight - realHeight) / 2
        val imageRight = imageLeft + realWidth
        val imageBottom = imageTop + realHeight
        mImageRect?.set(imageLeft, imageTop, imageRight, imageBottom)

    }

    /**
     * 返回马赛克最终结果
     *
     * @return 马赛克最终结果
     */
    fun getMosaicBitmap(): Bitmap? {
        if (bmMosaicLayer == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(
            mImageWidth, mImageHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bmBaseLayer!!, 0f, 0f, null)
        canvas.drawBitmap(bmMosaicLayer!!, 0f, 0f, null)
        canvas.save()
        return bitmap
    }

    private fun dp2px(dip: Int): Int {
        val context = this.context
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip.toFloat(), resources.displayMetrics
        ).roundToInt()
    }

    fun setOnMosaicStateListener(listener: OnMosaicStateListener) {
        this.listener = listener
    }

    interface OnMosaicStateListener {
        fun mosaicAmount(amountSize: Int?)
    }

}