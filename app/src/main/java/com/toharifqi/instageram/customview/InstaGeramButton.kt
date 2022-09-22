package com.toharifqi.instageram.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.toharifqi.instageram.R

class InstaGeramButton : AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColorEnabled: Int = 0
    private var txtColorDisabled: Int = 0

    constructor(context: Context) : super(context) { init() }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        super.onDraw(canvas)

        background = if(isEnabled) enabledBackground else disabledBackground
        textSize = 12f
        gravity = Gravity.CENTER

        if (isEnabled) {
            text = this.text
            setTextColor(txtColorEnabled)
        } else {
            text = context.getString(R.string.button_disabled)
            setTextColor(txtColorDisabled)
        }
    }

    private fun init() {
        txtColorEnabled = ContextCompat.getColor(context, android.R.color.background_light)
        txtColorDisabled = ContextCompat.getColor(context, R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable
    }

}