package com.example.kumandra.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.kumandra.R
import com.example.kumandra.ui.LoginActivity


class PasswordET : AppCompatEditText {

    constructor(context: Context) : super(context) {
        PassEditText()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        PassEditText()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        PassEditText()
    }

    private fun PassEditText() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                LoginActivity.isPassError(false)

                if (!text.isNullOrEmpty() && text.length < 8) {
                    error = context.getString(R.string.error_pas)
                }
            }

            override fun afterTextChanged(text: Editable?) {
            }
        })
    }
}