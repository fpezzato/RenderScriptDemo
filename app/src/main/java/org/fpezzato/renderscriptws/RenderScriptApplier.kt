package org.fpezzato.renderscriptws

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.util.Log
import com.example.android.renderscriptintrinsic.ScriptC_threshold
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

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

    init {
        renderscript = RenderScript.create(context)
        inAllocation = Allocation.createFromBitmap(renderscript, bitmapIn)
        outAllocation = Allocation.createFromBitmap(renderscript, bitmapIn) //TODO Check.
        thresholdScript = ScriptC_threshold(renderscript)

        trigger
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map { config ->
                    thresholdScript._thresholdValue = 0.4f
                    val time = System.currentTimeMillis()
                    thresholdScript.forEach_filter(inAllocation, outAllocation)
                    val delta = System.currentTimeMillis() - time
                    Log.wtf("YOOOO","delta= $delta")
                }
                .subscribe {
                    outAllocation.copyTo(bitmapOut)
                    filterPerformed.onNext(Unit)
                }
    }

    fun performFilter(config: Config) {
        trigger.onNext(config)
    }

    fun result(): Observable<Unit> {
        return filterPerformed
    }

    class Config {
    }


}
