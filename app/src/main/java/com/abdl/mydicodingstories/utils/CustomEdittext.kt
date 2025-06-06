package com.abdl.mydicodingstories.utils

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.abdl.mydicodingstories.R

class CustomEdittext : AppCompatEditText, View.OnTouchListener {

    private var edtPass: EditText? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        edtPass = findViewById(R.id.edt_pass)

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! < 8) {
                    edtPass?.error = "Minimal 8 karakter"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing.
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}