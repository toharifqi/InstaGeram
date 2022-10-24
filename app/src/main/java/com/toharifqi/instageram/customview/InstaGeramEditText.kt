package com.toharifqi.instageram.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.toharifqi.instageram.R

class InstaGeramEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        setOnTouchListener(this)
        val inputType = this.inputType

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                when {
                    s.toString().isEmpty() -> showEmptyError()
                    inputType == PASSWORD_TYPE && s.count() < 6 -> showPasswordError()
                    inputType == EMAIL_TYPE && !Patterns.EMAIL_ADDRESS.matcher(s).matches() -> showInvalidEmailError()
                }

                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showInvalidEmailError() {
        val errorMessage = context.getString(R.string.error_invalid_email)
        error = errorMessage
    }

    private fun showPasswordError() {
        val errorMessage = context.getString(R.string.error_text_password)
        error = errorMessage
    }

    private fun showEmptyError() {
        val errorMessage = context.getString(R.string.error_text_empty)
        error = errorMessage
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText, topOfTheText, endOfTheText, bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(
                            context, R.drawable.ic_baseline_close_24
                        ) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP   -> {
                        clearButtonImage = ContextCompat.getDrawable(
                            context, R.drawable.ic_baseline_close_24
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else                    -> return false
                }
            } else return false
        }
        return false
    }
}

private const val PASSWORD_TYPE = 0x00000081
private const val EMAIL_TYPE = 0x00000021
