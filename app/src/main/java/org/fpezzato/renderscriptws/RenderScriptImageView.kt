package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

class RenderScriptImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private lateinit var renderScriptApplier: RenderScriptApplier
    private lateinit var bitmapIn: Bitmap
    private lateinit var bitmapOut: Bitmap

    init {
        setWillNotDraw(false)
        allocateBitmaps()
        renderScriptApplier = RenderScriptApplier(this.context, bitmapIn, bitmapOut)
        setImageBitmap(bitmapOut)
    }

    var currentValue = 0
    var growing = true

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        currentValue += if (growing) 1 else -1
        if (currentValue >= 100) {
            growing = false
        }
        if (currentValue <= 0) {
            growing = true
        }

        renderScriptApplier.process(
                currentProgress(currentValue.toFloat(), 0f, 1f),
                currentProgress(currentValue.toFloat(), 0.1f, 25f)
        )
        setImageBitmap(bitmapOut)
        invalidate()
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


