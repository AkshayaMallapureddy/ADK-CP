package com.example.intentdemo

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.workDataOf
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import java.util.concurrent.TimeUnit

class Third_Activity : AppCompatActivity() {
    private val myReceiver = MyReciever()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_battery: Button = findViewById(R.id.buttonBattery)
        btn_battery.setOnClickListener {
            val intent = Intent(this, BatteryInfoActivity::class.java)
            startActivity(intent)
        }

        val btn_logout: Button = findViewById(R.id.button4)
        btn_logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        val btn_contacts: Button = findViewById(R.id.button3)
        btn_contacts.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse("content://contacts/people"), "vnd.android.cursor.dir/contact")
            startActivity(intent)
        }
        val btn_dailer: Button = findViewById(R.id.button2)
        btn_dailer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9999999999"))
            startActivity(intent)
        }
        val btn_camera: Button = findViewById(R.id.button)
        btn_camera.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }
        val btn_service: Button = findViewById(R.id.button7)
        btn_service.setOnClickListener {
            val intent = Intent(this, DemoService::class.java)
            startService(intent)
        }
        val btn_stop_service: Button = findViewById(R.id.button8)
        btn_stop_service.setOnClickListener {
            val intent = Intent(this, DemoService::class.java)
            stopService(intent)
        }
        val btn_browser: Button = findViewById(R.id.button5)
        btn_browser.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }

        val btn_worker: Button = findViewById(R.id.button6)
        val inputData = workDataOf("LogWorkerMessage" to "This is a log message")
        btn_worker.setOnClickListener {
            //val workRequest = OneTimeWorkRequestBuilder<LogWorker>()
            val workRequest=PeriodicWorkRequestBuilder<LogWorker>(15, TimeUnit.MINUTES)
                .setInputData(inputData)
                .build()
            val workManager = WorkManager.getInstance(this)
            workManager.enqueueUniquePeriodicWork("LogWorker", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest)
            //workManager.enqueue(workRequest)
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
        Log.d("BroadcastReceiver", "Receiver Registered in Third_Activity")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
        Log.d("BroadcastReceiver", "Receiver Unregistered in Third_Activity")
    }
}