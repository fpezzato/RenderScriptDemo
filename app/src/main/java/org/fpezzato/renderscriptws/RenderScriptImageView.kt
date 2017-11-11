package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

class RenderScriptImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var renderScriptApplier: RenderScriptApplier
    private lateinit var bitmapIn: Bitmap
    private lateinit var bitmapOut: Bitmap
    var applyBlur = true

    init {
        setWillNotDraw(false)
        allocateBitmaps()
        renderScriptApplier = RenderScriptApplier(this.context, bitmapIn, bitmapOut)
        setImageBitmap(bitmapOut)
    }

    private var currentValue = 0
    private var growing = true
    private var lastDrawMsValues = LongArray(10)
    private var lastDrawMsIdx = 0
    private val textPaint: Paint = Paint(Color.BLACK).apply { textSize = 30f }

    override fun onDraw(canvas: Canvas?) {
        val currentTimeMillis = System.currentTimeMillis()

        super.onDraw(canvas)
        calculatePercentage()

        renderScriptApplier.process(
                applyBlur,
                currentProgress(currentValue.toFloat(), 0f, 1f),
                currentProgress(currentValue.toFloat(), 0.1f, 25f) // 25 is max supported🤷
        )
        setImageBitmap(bitmapOut)
        invalidate()


        val delta = System.currentTimeMillis() - currentTimeMillis
        canvas?.drawText(computeFPS(delta), 100f, 100f, textPaint)
    }

    private fun calculatePercentage() {
        currentValue += if (growing) 1 else -1
        if (currentValue >= 100) {
            growing = false
        }
        if (currentValue <= 0) {
            growing = true
        }
    }

    private fun computeFPS(delta: Long): String {
        lastDrawMsIdx++
        if (lastDrawMsIdx >= lastDrawMsValues.size) {
            lastDrawMsIdx = 0
        }
        lastDrawMsValues[lastDrawMsIdx] = delta
        val media = (lastDrawMsValues.sum() / lastDrawMsValues.size)
        return "ms:${media.toInt()}"
    }

    private fun allocateBitmaps() {
        bitmapIn = loadBitmap(R.drawable.photo1)
        bitmapOut = Bitmap.createBitmap(
                bitmapIn.width,
                bitmapIn.height,
                bitmapIn.config)
    }

    private fun loadBitmap(resource: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeResource(resources, resource, options)
    }

    private fun currentProgress(current: Float, min: Float, max: Float): Float {
        return ((max - min) * (current / 100) + min)
    }
}


