package com.jepster.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path: Path = Path()

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val rowText = rowFour + rectInset + clipRectBottom

    private val rectF: RectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (counter in 0..8) doInstructions(canvas, counter)
    }

    private fun drawARect(canvas: Canvas) {
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        canvas.drawColor(Color.WHITE)
        paint.color = Color.RED
        canvas.drawLine(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom, paint)
        paint.color = Color.GREEN
        canvas.drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)
        paint.color = Color.BLUE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(context.getString(R.string.clipping), clipRectRight, textOffset, paint)
    }

    @Suppress("DEPRECATION")
    private fun doInstructions(canvas: Canvas, instructionSet: Int) {

        val columnChoice: Float = if (instructionSet < 7) {
            if (instructionSet % 2 == 0) columnOne else columnTwo
        } else columnTwo

        val rowChoice: Float = if (instructionSet < 7) {
            when (instructionSet / 2) {
                0 -> rowOne
                1 -> rowTwo
                2 -> rowThree
                else -> rowFour
            }
        } else rowText

        if (instructionSet == 0) canvas.drawColor(Color.GRAY)

        canvas.save()

        if (instructionSet == 7) {
            paint.color = Color.GREEN
            paint.textAlign = Paint.Align.LEFT
        } else if (instructionSet == 8) {
            paint.color = Color.YELLOW
            paint.textAlign = Paint.Align.RIGHT
        }

        canvas.translate(columnChoice, rowChoice)

        when (instructionSet) {
            1 -> {
                canvas.clipRect(
                    2 * rectInset,
                    2 * rectInset,
                    clipRectRight - 2 * rectInset,
                    clipRectBottom - 2 * rectInset
                )
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    canvas.clipRect(
                        4 * rectInset,
                        4 * rectInset,
                        clipRectRight - 4 * rectInset,
                        clipRectBottom - 4 * rectInset,
                        Region.Op.DIFFERENCE
                    )
                } else {
                    canvas.clipOutRect(
                        4 * rectInset,
                        4 * rectInset,
                        clipRectRight - 4 * rectInset,
                        clipRectBottom - 4 * rectInset
                    )
                }
            }
            2 -> {
                path.rewind()
                path.addCircle(
                    circleRadius,
                    clipRectBottom - circleRadius,
                    circleRadius,
                    Path.Direction.CCW
                )
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    canvas.clipPath(path, Region.Op.DIFFERENCE)
                } else {
                    canvas.clipOutPath(path)
                }
            }
            3 -> {
                canvas.clipRect(
                    clipRectLeft,
                    clipRectTop,
                    clipRectRight - smallRectOffset,
                    clipRectBottom - smallRectOffset
                )
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    canvas.clipRect(
                        clipRectLeft + smallRectOffset,
                        clipRectTop + smallRectOffset,
                        clipRectRight,
                        clipRectBottom,
                        Region.Op.INTERSECT
                    )
                } else {
                    canvas.clipRect(
                        clipRectLeft + smallRectOffset,
                        clipRectTop + smallRectOffset,
                        clipRectRight,
                        clipRectBottom
                    )
                }
            }
            4 -> {
                path.rewind()
                path.addCircle(
                    clipRectLeft + rectInset + circleRadius,
                    clipRectTop + rectInset + circleRadius,
                    circleRadius, Path.Direction.CCW
                )
                path.addRect(
                    clipRectRight / 2 - circleRadius,
                    clipRectTop + circleRadius + rectInset,
                    clipRectRight / 2 + circleRadius,
                    clipRectBottom - rectInset, Path.Direction.CCW
                )
                canvas.clipPath(path)
            }
            5 -> {
                path.rewind()
                path.addRoundRect(
                    rectF,
                    clipRectRight / 4,
                    clipRectRight / 4,
                    Path.Direction.CCW
                )
                canvas.clipPath(path)
            }
            6 -> {
                canvas.clipRect(
                    2 * rectInset,
                    2 * rectInset,
                    clipRectRight - 2 * rectInset,
                    clipRectBottom - 2 * rectInset
                )
            }
            7 -> {
                canvas.drawText(
                    context.getString(R.string.translated),
                    clipRectLeft,
                    clipRectTop,
                    paint
                )
            }
            8 -> {
                canvas.skew(0.2f, 0.3f)
                canvas.drawText(
                    context.getString(R.string.skewed),
                    clipRectLeft,
                    clipRectTop,
                    paint
                )
            }
            else -> {
            }
        }
        if (instructionSet < 7) drawARect(canvas)
        canvas.restore()
    }
}
