package org.fpezzato.renderscriptws

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.CompoundButton


class MainActivity : AppCompatActivity() {

    private lateinit var imageView: RenderScriptImageView
    private lateinit var enableBlur: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableBlur = findViewById<CheckBox>(R.id.enable_blur).apply {
            setOnCheckedChangeListener(checkedChangeListener)
        }


        imageView = findViewById<RenderScriptImageView>(R.id.imageView)
    }

    private val checkedChangeListener: CompoundButton.OnCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { _, _ ->
                imageView.applyBlur = enableBlur.isChecked
            }
}
