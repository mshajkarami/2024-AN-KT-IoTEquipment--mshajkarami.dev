package com.irisaco.iotequipment.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.irisaco.iotequipment.R
import com.irisaco.iotequipment.databinding.CustomButtonAppBinding

class AppButton(context: Context, attr: AttributeSet) : FrameLayout(context, attr) {

    private var binding: CustomButtonAppBinding =
        CustomButtonAppBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initialize(attr)
    }

    private fun initialize(attr: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AppButton)
        val text = typedArray.getString(R.styleable.AppButton_android_text)

        val style = typedArray.getInt(R.styleable.AppButton_buttonStyle, 0)

        binding.btnText.text = text

        when (style) {
            0 -> { // Primary
                binding.btnText.setBackgroundResource(R.drawable.rec_btn_primary)
                binding.btnText.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
            }
            1 -> { // Secondary
                binding.btnText.setBackgroundResource(R.drawable.rec_btn_secondary)
                binding.btnText.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onSecondary))
            }
        }
        typedArray.recycle()

    }
    fun setText(text: String) {
        binding.btnText.text = text
    }

    fun getText(): String {
        return binding.btnText.text.toString()
    }

}