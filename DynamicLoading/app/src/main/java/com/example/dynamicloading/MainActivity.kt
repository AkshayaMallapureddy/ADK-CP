package com.example.dynamicloading

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val masterLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        setContentView(masterLayout)
        
        val row1 = InputRow(this, "Enter number 1")
        val row2 = InputRow(this, "Enter number 2")
        masterLayout.addView(row1)
        masterLayout.addView(row2)
        
        val addBtn = Button(this).apply {
            text = "+"
            textSize = 32f
            setPadding(10, 10, 10, 10)
        }
        val mulBtn = Button(this).apply {
            text = "-"
            textSize = 32f
            setPadding(10, 10, 10, 10)
        }
        val subBtn = Button(this).apply {
            text = "*"
            textSize = 32f
            setPadding(10, 10, 10, 10)
        }
        val divBtn = Button(this).apply {
            text = "/"
            textSize = 32f
            setPadding(10, 10, 10, 10)
        }
        
        val btnrow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(addBtn)
            addView(subBtn)
            addView(divBtn)
            addView(mulBtn)
        }
        masterLayout.addView(btnrow)
        
        val resRow = InputRow(this, "Result: ")
        masterLayout.addView(resRow)
        
        addBtn.setOnClickListener {
            val n1 = row1.numField.text.toString().toDoubleOrNull() ?: 0.0
            val n2 = row2.numField.text.toString().toDoubleOrNull() ?: 0.0
            resRow.numField.setText((n1 + n2).toString())
        }
        
        subBtn.setOnClickListener {
            val n1 = row1.numField.text.toString().toDoubleOrNull() ?: 0.0
            val n2 = row2.numField.text.toString().toDoubleOrNull() ?: 0.0
            resRow.numField.setText((n1 - n2).toString())
        }

        divBtn.setOnClickListener {
            val n1 = row1.numField.text.toString().toDoubleOrNull() ?: 0.0
            val n2 = row2.numField.text.toString().toDoubleOrNull() ?: 0.0
            if (n2 != 0.0) {
                resRow.numField.setText((n1 / n2).toString())
            } else {
                resRow.numField.setText("Error")
            }
        }
    }
}
