package com.example.simplecalculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText1: EditText = findViewById(R.id.editText1)
        val editText2: EditText = findViewById(R.id.editText2)
        val resText3: EditText = findViewById(R.id.resText3)

        val addBtn: Button = findViewById(R.id.addBtn)
        val subBtn: Button = findViewById(R.id.subBtn)
        val mulBtn: Button = findViewById(R.id.mulBtn)
        val divBtn: Button = findViewById(R.id.divBtn)

        addBtn.setOnClickListener {
            resText3.setText(operate(editText1.text.toString(), editText2.text.toString(), "+"))
        }
        subBtn.setOnClickListener {
            resText3.setText(operate(editText1.text.toString(), editText2.text.toString(), "-"))
        }
        mulBtn.setOnClickListener {
            resText3.setText(operate(editText1.text.toString(), editText2.text.toString(), "*"))
        }
        divBtn.setOnClickListener {
            resText3.setText(operate(editText1.text.toString(), editText2.text.toString(), "/"))
        }
        Log.d("LifeCycleDemo", "onCreate() Method called")
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeCycleDemo", "onStart() Method called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeCycleDemo", "onResume() Method called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeCycleDemo", "onPause() Method called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeCycleDemo", "onStop() Method called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeCycleDemo", "onDestroy() Method called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("LifeCycleDemo", "onRestart() Method called")
    }

    private fun operate(num1: String, num2: String, operation: String): String {
        if (num1.isEmpty() || num2.isEmpty()) {
            Toast.makeText(this, "Please enter both numbers", Toast.LENGTH_SHORT).show()
            return ""
        }

        val n1 = num1.toDoubleOrNull()
        val n2 = num2.toDoubleOrNull()

        if (n1 == null || n2 == null) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            return ""
        }

        return when (operation) {
            "+" -> (n1 + n2).toString()
            "-" -> (n1 - n2).toString()
            "*" -> (n1 * n2).toString()
            "/" -> {
                if (n2 == 0.0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                    ""
                } else {
                    (n1 / n2).toString()
                }
            }
            else -> ""
        }
    }
}
