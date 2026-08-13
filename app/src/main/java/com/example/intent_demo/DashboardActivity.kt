package com.example.intentdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.activity.enableEdgeToEdge
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvBatteryLevelMain: TextView
    private lateinit var tvChargingStatusMain: TextView

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(it)
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_PLAY"
                data = it
                putExtra("EXTRA_FILE_NAME", fileName)
            }
            startService(intent)
            Toast.makeText(this, "Playing: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "Unknown Song"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = (level * 100 / scale.toFloat()).toInt()
                tvBatteryLevelMain.text = getString(R.string.battery_level, batteryPct)

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                tvChargingStatusMain.text = getString(R.string.charging_status, isCharging.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        tvBatteryLevelMain = findViewById(R.id.tvBatteryLevelMain)
        tvChargingStatusMain = findViewById(R.id.tvChargingStatusMain)

        val dashboardRoot = findViewById<android.view.View>(R.id.dashboard_root)
        ViewCompat.setOnApplyWindowInsetsListener(dashboardRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnContacts).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDialer).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234567890"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnContactDetail).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/1"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_START_COUNTER"
            }
            startService(intent)
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStopService).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_STOP_COUNTER"
            }
            startService(intent)
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStartMusic).setOnClickListener {
            pickAudioLauncher.launch("audio/*")
        }

        findViewById<Button>(R.id.btnStopMusic).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_STOP_MUSIC"
            }
            startService(intent)
            Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnDownload).setOnClickListener {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Any network (Wi-Fi or Mobile)
                .build()

            val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueue(downloadWorkRequest)
            Toast.makeText(this, "Starting Background Download...", Toast.LENGTH_SHORT).show()

            // Observing status to show Toast when finished
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadWorkRequest.id)
                .observe(this) { workInfo ->
                    if (workInfo != null && workInfo.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                        Toast.makeText(this, "Background Download Finished!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        findViewById<Button>(R.id.btnBatteryInfo).setOnClickListener {
            val intent = Intent(this, BatteryInfoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = ContextCompat.registerReceiver(this, batteryReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        if (stickyIntent != null) {
            // Trigger update immediately
            batteryReceiver.onReceive(this, stickyIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }
}
