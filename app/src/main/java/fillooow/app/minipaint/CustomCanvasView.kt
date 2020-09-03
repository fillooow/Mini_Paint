package fillooow.app.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

private const val STROKE_WIDTH = 12f

class CustomCanvasView(context: Context) : View(context) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    // caching x and y coordinates of the current touch event (the [MotionEvent] coordinates)
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private var path = Path()

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val paint = Paint().apply {

        color = drawColor
        isAntiAlias = true // smooth out edges
        isDither = true // понижает количество цветов до 256 (вроде). Короч, понижение размерности
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    /**
     * [touchTolerance] - минимальная дистанция движения пальца, после которой
     * засчитываем перемещение и отрисовываем линию
     *
     * [scaledTouchSlop] - вернет дистанцию в пикселях, после которой система решит,
     * что произошел евент (а не "шумовое" движение пальца)
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)

        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {

            ACTION_DOWN -> touchStart()
            ACTION_MOVE -> touchMove()
            ACTION_UP -> touchUp()
        }

        return true
    }

    private fun touchStart() {

        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {

        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        if (dx >= touchTolerance || dy >= touchTolerance) {

            /**
             * [Path.quadTo] создает кривую Безье, начиная из последней точки,
             * стремясь к точке x1,y2 и заканчивает ее в x2,y2
             */
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            // draw the path in the extra bitmap to cache it
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {

        path.reset()
    }
}