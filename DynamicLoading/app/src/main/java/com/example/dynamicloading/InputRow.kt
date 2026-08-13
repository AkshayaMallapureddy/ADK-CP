package com.example.dynamicloading

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class InputRow @JvmOverloads constructor(
    context: Context,
    label: String = "",
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val labview: TextView = TextView(context).apply {
        text = label
        textSize = 24f
        setPadding(10, 10, 10, 10)
    }
    
    val numField: EditText = EditText(context).apply {
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    }

    init {
        orientation = HORIZONTAL
        addView(labview)
        addView(numField)
    }
}
