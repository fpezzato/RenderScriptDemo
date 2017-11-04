package org.fpezzato.renderscriptws

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import io.reactivex.disposables.CompositeDisposable


class MainActivity : AppCompatActivity() {


    private lateinit var bitmapIn: Bitmap
    private lateinit var bitmapOut: Bitmap
    private lateinit var imageView: ImageView
    private lateinit var renderScriptApplier: RenderScriptApplier
    var composite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        allocateBitmaps()
        imageView = findViewById(R.id.imageView)
        imageView.setImageBitmap(bitmapOut)

        renderScriptApplier = RenderScriptApplier(this, bitmapIn, bitmapOut)
    }

    override fun onResume() {
        super.onResume()
        composite.add(renderScriptApplier.result()
                .subscribe {
                    imageView.setImageBitmap(bitmapOut)
                    imageView.invalidate()
                }
        )
        renderScriptApplier.performFilter(RenderScriptApplier.Config())
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
}
