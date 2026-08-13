package com.example.intentdemo

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Second_Activity : AppCompatActivity() {
    private val myReceiver = MyReciever()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val resultView: TextView = findViewById(R.id.resultView)
        val uname = intent.getStringExtra("uname")
        val status = intent.getStringExtra("status")

        if (status == "SUCCESS") {
            resultView.text = "Login Successful!\nWelcome $uname"
        } else {
            resultView.text = "Login Failed for $uname"
        }

        val okBtn: Button = findViewById(R.id.button)
        okBtn.setOnClickListener {
            if (status == "SUCCESS") {
                val intent = Intent(this, Third_Activity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        ContextCompat.registerReceiver(this, myReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        Log.d("BroadcastReceiver", "Receiver Registered in Second_Activity")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
        Log.d("BroadcastReceiver", "Receiver Unregistered in Second_Activity")
    }
}