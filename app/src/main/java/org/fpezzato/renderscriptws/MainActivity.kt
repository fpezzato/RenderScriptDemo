package org.fpezzato.renderscriptws

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.SeekBar
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity() {


    private lateinit var bitmapIn: Bitmap
    private lateinit var bitmapOut: Bitmap
    private lateinit var imageView: ImageView
    private lateinit var seekbarThreshold: SeekBar
    private lateinit var seekbarBlur: SeekBar
    private lateinit var renderScriptApplier: RenderScriptApplier

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
        seekbarBlur = findViewById(R.id.seekBarBlur)
        seekbarBlur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        launch(UI, CoroutineStart.ATOMIC) {
            for (i in 1..200) {
                var j = i
                if (j > 100) {
                    j = 200 - j
                }
                val job = async() {
                    renderScriptApplier.process(
                            currentProgress(j.toFloat(), 0f, 1f),
                            currentProgress(j.toFloat(), 0.1f, 25f)
                    )
                }
                job.await()
                imageView.setImageBitmap(bitmapOut)
                imageView.invalidate()
            }

        }
    }

    private fun applyTranformations() {
        async(UI) {
            val job = async() {
                renderScriptApplier.process(
                        currentProgress(seekbarThreshold, 0f, 1f),
                        currentProgress(seekbarBlur, 0.1f, 25f)
                )
            }
            job.await()
            imageView.setImageBitmap(bitmapOut)
            imageView.invalidate()
        }
    }


    private fun allocateBitmaps() {
        bitmapIn = loadBitmap(R.drawable.photo1)
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

    private fun currentProgress(seekBar: SeekBar, min: Float, max: Float): Float {
        return currentProgress(seekBar.progress.toFloat(), min, max)
    }

    private fun currentProgress(current: Float, min: Float, max: Float): Float {
        return ((max - min) * (current / 100) + min)
    }

}
