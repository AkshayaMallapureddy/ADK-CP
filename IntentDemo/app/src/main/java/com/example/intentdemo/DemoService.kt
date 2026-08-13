package com.example.intentdemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.Timer
import java.util.TimerTask

class DemoService : Service() {
    private var timer = Timer()
    private var counter = 0
    private var isStarted: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isStarted) {
            Log.d("ServiceLog", "Service already started")
        } else {
            isStarted = true
            timer.schedule(object : TimerTask() {
                override fun run() {
                    counter++
                    Log.d("ServiceLog", "Counter:$counter")
                }
            }, 0, 1000)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        Log.d("ServiceLog", "Service Stopped")
    }
}