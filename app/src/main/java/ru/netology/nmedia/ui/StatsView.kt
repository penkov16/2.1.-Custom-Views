package ru.netology.nmedia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import java.lang.Double.sum
import java.util.Collections.list
import kotlin.math.min
import kotlin.random.Random


class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()
    private var emptyColor = rgb(238, 238, 238)
    private var bkColor = rgb(255, 255, 255)

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            bkColor = getColor(R.styleable.StatsView_bkColor, bkColor)
            emptyColor = getColor(R.styleable.StatsView_emptyColor, emptyColor)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(
                    R.styleable.StatsView_color1,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color2,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color3,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color4,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color5,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color6,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color7,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color8,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color9,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color10,
                    randomColor()
                )
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var maxValue: Float = -1F
        set(value) {
            field = if (value >= 0) value else data.sum()
            invalidate()
        }

    var data: List<Float> = emptyList()
        set(value) {
            maxValue = if (maxValue <= 0) value.sum() else maxValue
            field = value
            updateColorTable()
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    private fun updateColorTable() {
        if (data.size > colors.size) {
            val newColorList = mutableListOf<Int>()
            repeat(data.size - colors.size) {
                newColorList.add(randomColor())
            }
            colors = colors + newColorList
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        paint.color = emptyColor
        canvas.drawCircle(center.x, center.y, radius, paint)

        //разбиваем отрисовку дуги на две равные половины участка
        //сначала рисуем концовку
        val dataPct = data.map { it / maxValue }
        var startFrom = -90F
        for ((index, datum) in dataPct.withIndex()) {
            val angle = 360F * datum
            paint.color = colors[index]
            canvas.drawArc(oval, startFrom + angle / 2, angle / 2, false, paint)
            startFrom += angle
        }
        //потом рисуем начало дуги
        startFrom = -90F
        for ((index, datum) in dataPct.withIndex()) {
            val angle = 360F * datum
            paint.color = colors[index]
            canvas.drawArc(oval, startFrom, angle / 2, false, paint)
            startFrom += angle
        }

        canvas.drawText(
            "%.2f%%".format(dataPct.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}