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
        context: Context,
        bitmapIn: Bitmap,
        private val bitmapOut: Bitmap
) {

    private var renderscript: RenderScript = RenderScript.create(context)
    private var inAllocation: Allocation
    private var outAllocation: Allocation
    private var allocationTmp: Allocation
    private var thresholdScript: ScriptC_threshold
    private var blurIntrinsicScript: ScriptIntrinsicBlur
    private var lutIntrinsicScript: ScriptIntrinsicLUT

    init {
        inAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        outAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        allocationTmp = Allocation.createFromBitmap(renderscript, bitmapIn)
        thresholdScript = ScriptC_threshold(renderscript)
        blurIntrinsicScript = ScriptIntrinsicBlur.create(renderscript, Element.U8_4(renderscript));
        lutIntrinsicScript = ScriptIntrinsicLUT.create(renderscript, Element.U8_4(renderscript))
    }

    fun process(blurEnabled: Boolean = true, threshold: Float, blurRadius: Float) {
        thresholdScript._thresholdValue = threshold
        thresholdScript.forEach_filter(inAllocation, allocationTmp)

        lutIntrinsicScript.setBlue(100, 255)
        lutIntrinsicScript.setGreen(20, 250)
        lutIntrinsicScript.forEach(allocationTmp, outAllocation)

        if (blurEnabled) {
            blurIntrinsicScript.setRadius(blurRadius)
            blurIntrinsicScript.setInput(outAllocation)
            blurIntrinsicScript.forEach(allocationTmp)
            allocationTmp.copyTo(bitmapOut)
        } else {
            outAllocation.copyTo(bitmapOut)
        }

    }

}
