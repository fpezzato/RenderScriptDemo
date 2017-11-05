package org.fpezzato.renderscriptws

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.SeekBar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


class MainActivity : AppCompatActivity() {


    private lateinit var bitmapIn: Bitmap
    private lateinit var bitmapOut: Bitmap
    private lateinit var imageView: ImageView
    private lateinit var seekbarThreshold: SeekBar
    private lateinit var renderScriptApplier: RenderScriptApplier
    var composite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        allocateBitmaps()
        imageView = findViewById(R.id.imageView)
        imageView.setImageBitmap(bitmapOut)
        seekbarThreshold = findViewById(R.id.seekBarThreshold)
        seekbarThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                applyTranformations()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        renderScriptApplier = RenderScriptApplier(this, bitmapIn, bitmapOut)
    }

    override fun onResume() {
        super.onResume()
        /* composite.add(renderScriptApplier.result()
                 .subscribe {
                     imageView.setImageBitmap(bitmapOut)
                     imageView.invalidate()
                 }
         )
 */
        applyTranformations()


        /* Observable.interval(100, TimeUnit.MILLISECONDS).subscribe { it ->
             val max = 1.0f
             val min = 0f
             val f = ((max - min) * (it%100 / 100.0) + min).toFloat()
             renderScriptApplier.performFilter(RenderScriptApplier.Config(f))
         }*/
    }

    private fun applyTranformations() {
        async(UI) {
            val job = async() {
                renderScriptApplier.workload(currentProgress(seekbarThreshold,0f,1f))
                //imageView.setImageBitmap(bitmapOut)
                // imageView.invalidate()
            }
            job.await()
            imageView.setImageBitmap(bitmapOut)
            imageView.invalidate()
        }
    }

    override fun onPause() {
        composite.clear()
        super.onPause()
    }

    private fun allocateBitmaps() {
        bitmapIn = loadBitmap(R.drawable.data)
        bitmapOut = Bitmap.createBitmap(
                bitmapIn.getWidth(),
                bitmapIn.getHeight(),
                bitmapIn.getConfig())
    }


    private fun loadBitmap(resource: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeResource(resources, resource, options)
    }

    private fun currentProgress(seekBar: SeekBar, min :Float, max: Float): Float {
        return  ((max - min) * (seekBar.progress / 100.0) + min).toFloat()
    }

}
