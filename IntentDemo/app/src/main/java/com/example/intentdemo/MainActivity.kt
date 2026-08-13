package com.example.intentdemo

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val myReceiver = MyReciever()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val unameField: EditText = findViewById(R.id.etUsername)
        val passField: EditText = findViewById(R.id.etPassword)
        val loginBtn: Button = findViewById(R.id.btnLogin)
        val clearBtn: Button = findViewById(R.id.btnClear)

        clearBtn.setOnClickListener {
            unameField.text.clear()
            passField.text.clear()
        }

        loginBtn.setOnClickListener {
            val uname = unameField.text.toString()
            val pass = passField.text.toString()

            if (uname.isEmpty() || pass.isEmpty()) {
                if (uname.isEmpty()) unameField.error = "Username Required"
                if (pass.isEmpty()) passField.error = "Password Required"
            } else {
                val status: String
                if (pass == uname) {
                    status = "SUCCESS"
                } else {
                    status = "FAILURE"
                }

                val intent = Intent(this, Second_Activity::class.java)
                intent.putExtra("uname", uname)
                intent.putExtra("status", status)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        // For Android 14+, we must specify RECEIVER_EXPORTED for system broadcasts
        ContextCompat.registerReceiver(this, myReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        Log.d("BroadcastReceiver", "Receiver Registered in MainActivity")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
        Log.d("BroadcastReceiver", "Receiver Unregistered in MainActivity")
    }
}