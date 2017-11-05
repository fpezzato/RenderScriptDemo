package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import com.example.android.renderscriptintrinsic.ScriptC_threshold
import java.util.concurrent.atomic.AtomicBoolean

class RenderScriptApplier(
        private val context: Context,
        private val bitmapIn: Bitmap,
        private val bitmapOut: Bitmap
) {

    private var renderscript: RenderScript
    private var inAllocation: Allocation
    private var outAllocation: Allocation
    private var thresholdScript: ScriptC_threshold
    private var blurIntrinsicScript: ScriptIntrinsicBlur

    init {
        renderscript = RenderScript.create(context)
        inAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        outAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        thresholdScript = ScriptC_threshold(renderscript)
        blurIntrinsicScript = ScriptIntrinsicBlur.create(renderscript, Element.U8_4(renderscript));
    }

    val running = AtomicBoolean(false)

    suspend fun process(threshold: Float, blurRadius: Float) {
        synchronized(this) {
            if(running.get()){
                return
            }
            running.set(true)
            thresholdScript._thresholdValue = threshold
            val time = System.currentTimeMillis()

            thresholdScript.forEach_filter(inAllocation, outAllocation)

            blurIntrinsicScript.setRadius(blurRadius)
            blurIntrinsicScript.setInput(outAllocation)
            blurIntrinsicScript.forEach(outAllocation)

            val delta = System.currentTimeMillis() - time
            outAllocation.copyTo(bitmapOut)

            Log.wtf("YOOOO", "delta= $delta")
            running.set(false)
        }
    }
}
