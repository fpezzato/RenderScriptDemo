package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.util.Log
import com.example.android.renderscriptintrinsic.ScriptC_threshold
import io.reactivex.subjects.PublishSubject
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
    private val trigger = PublishSubject.create<Config>()
    private val filterPerformed = PublishSubject.create<Unit>()
    private var isRunning = AtomicBoolean(false)

    init {
        renderscript = RenderScript.create(context)
        inAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        outAllocation = Allocation.createFromBitmap(renderscript, bitmapIn) //TODO Check.
        thresholdScript = ScriptC_threshold(renderscript)

        /*trigger
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { !isRunning.get() }
                .map { config ->
                    synchronized(this){
                        isRunning.set(true)
                        thresholdScript._thresholdValue = config.threshold
                        val time = System.currentTimeMillis()
                        thresholdScript.forEach_filter(inAllocation, outAllocation)
                        val delta = System.currentTimeMillis() - time
                        Log.wtf("YOOOO", "delta= $delta")
                        isRunning.set(false)
                    }

                }
                .subscribe {
                    outAllocation.copyTo(bitmapOut)
                    filterPerformed.onNext(Unit)
                }*/
    }

    suspend fun workload(n: Float): Unit {
        thresholdScript._thresholdValue = n
        val time = System.currentTimeMillis()
        thresholdScript.forEach_filter(inAllocation, outAllocation)
        val delta = System.currentTimeMillis() - time
        outAllocation.copyTo(bitmapOut)
        Log.wtf("YOOOO", "delta= $delta")
        return
    }


    data class Config(val threshold: Float)


}
