package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.ScriptIntrinsicLUT
import com.example.android.renderscriptintrinsic.ScriptC_threshold

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
    private var lutIntrinsicScript: ScriptIntrinsicLUT

    init {
        renderscript = RenderScript.create(context)
        inAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        outAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        thresholdScript = ScriptC_threshold(renderscript)
        blurIntrinsicScript = ScriptIntrinsicBlur.create(renderscript, Element.U8_4(renderscript));
        lutIntrinsicScript = ScriptIntrinsicLUT.create(renderscript, Element.U8_4(renderscript))
    }


    fun process(threshold: Float, blurRadius: Float) {
        thresholdScript._thresholdValue = threshold

        thresholdScript.forEach_filter(inAllocation, outAllocation)

        blurIntrinsicScript.setRadius(blurRadius)
        blurIntrinsicScript.setInput(outAllocation)
        blurIntrinsicScript.forEach(outAllocation)

      /*  lutIntrinsicScript.setBlue(100,255)
        lutIntrinsicScript.setGreen(20,250)
        lutIntrinsicScript.forEach(outAllocation,outAllocation)*/

        outAllocation.copyTo(bitmapOut)
    }
}
