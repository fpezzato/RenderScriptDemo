package org.fpezzato.renderscriptws

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.CompoundButton
import org.fpezzato.renderscriptws.RenderScriptApplier.Config


class MainActivity : AppCompatActivity() {

    private lateinit var imageView: RenderScriptImageView
    private lateinit var enableThreshold: CheckBox
    private lateinit var enableLut: CheckBox
    private lateinit var enableBlur: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableBlur = findViewById<CheckBox>(R.id.enable_blur).apply {
            setOnCheckedChangeListener(checkedChangeListener)
        }
        enableThreshold = findViewById<CheckBox>(R.id.enable_threshold).apply {
            setOnCheckedChangeListener(checkedChangeListener)
        }
        enableLut = findViewById<CheckBox>(R.id.enable_lut).apply {
            setOnCheckedChangeListener(checkedChangeListener)
        }

        imageView = findViewById<RenderScriptImageView>(R.id.imageView)
    }

    private val checkedChangeListener: CompoundButton.OnCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { _, _ ->
                val config = Config(
                        enableBlur.isChecked,
                        enableThreshold.isChecked,
                        enableLut.isChecked)
                imageView.config = config
            }
}
