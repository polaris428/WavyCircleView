package com.polaris.wavycircleview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.Nullable

class WavyCircleView @JvmOverloads constructor(
    context: Context?,
    val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    View(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var wavePaint: Paint

    private lateinit var circlePaint: Paint

    private var screenWidth = 0

    private var screenHeight = 0

    private val amplitude = 100
    private lateinit var path: Path

    private var progress = 0f
    private var textProgress = 0f
    private var flowingSpeed=7
    private val startPoint = Point()

    private var flowingColor = Color.parseColor("#6d2dcc")
    private var lineColor = Color.parseColor("#292929")

    fun setProgress(progress: Float) {
        textProgress = progress
        if (progress == 100f) {
            this.progress = progress + amplitude
        } else {
            this.progress = progress
        }
    }

    private fun init() {
        context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.WavyCircleView,
            0, 0
        )?.apply {
            try {
                flowingColor = getColor(R.styleable.WavyCircleView_flowingColor, flowingColor)
                lineColor = getColor(R.styleable.WavyCircleView_lineColor, lineColor)
                flowingSpeed = getInt(R.styleable.WavyCircleView_waveSpeed, flowingSpeed)
                progress = getFloat(R.styleable.WavyCircleView_progress, progress)
            } catch (e: Exception) {
                Log.e("WavyCircleView", "Error while parsing attributes", e)
            } finally {
                recycle()
            }
        } ?: Log.w("WavyCircleView", "StyledAttributes is null")
        wavePaint = Paint()
        wavePaint.isAntiAlias = true
        wavePaint.strokeWidth = 1f

        circlePaint = Paint()
        circlePaint.isAntiAlias = true
        circlePaint.color = lineColor
        circlePaint.strokeWidth = 10f
        circlePaint.style = Paint.Style.STROKE


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size =
            measureSize(400, widthMeasureSpec).coerceAtMost(measureSize(400, heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w
        screenHeight = h
        startPoint.x = -screenWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        clipCircle(canvas)
        drawCircle(canvas)
        drawWave(canvas)
        postInvalidateDelayed(10)
    }


    private fun drawWave(canvas: Canvas) {
        val height = (progress / 100 * screenHeight).toInt()
        startPoint.y = -height
        canvas.translate(0f, screenHeight.toFloat())
        path = Path()
        wavePaint.style = Paint.Style.FILL
        wavePaint.color = flowingColor
        val wave = screenWidth / 4
        path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        for (i in 0..3) {

            val startX = startPoint.x + i * wave * 2
            val endX = startX + 2 * wave
            if (i % 2 == 0) {
                path.quadTo(
                    ((startX + endX) / 2).toFloat(),
                    (startPoint.y + amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )
            } else {
                path.quadTo(
                    ((startX + endX) / 2).toFloat(),
                    (startPoint.y - amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )
            }
        }
        path.lineTo(screenWidth.toFloat(), (screenHeight / 2).toFloat())
        path.lineTo(-screenWidth.toFloat(), (screenHeight / 2).toFloat())
        path.lineTo(-screenWidth.toFloat(), 0f)
        path.close()
        canvas.drawPath(path, wavePaint)
        startPoint.x += flowingSpeed
        if (startPoint.x > 0) {
            startPoint.x = -screenWidth
        }
        path.reset()
    }


    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(
            (screenHeight / 2).toFloat(),
            (screenHeight / 2).toFloat(),
            (screenHeight / 2).toFloat(),
            circlePaint
        )
    }


    private fun clipCircle(canvas: Canvas) {
        val circlePath = Path()
        circlePath.addCircle(
            (screenWidth / 2).toFloat(),
            (screenHeight / 2).toFloat(),
            (screenHeight / 2).toFloat(),
            Path.Direction.CW
        )
        canvas.clipPath(circlePath)
    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.UNSPECIFIED -> result = defaultSize
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> result = size
            else -> {}
        }
        return result
    }

    fun setFlowingColor(colorString: String) {
        flowingColor = Color.parseColor(colorString)
    }

    fun setLineColor(colorString: String) {
        lineColor = Color.parseColor(colorString)
    }
    fun setSpeed(speed: Int){
        flowingSpeed=speed
    }

    init {
        init()
    }
}