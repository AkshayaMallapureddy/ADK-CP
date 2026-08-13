package com.example.intentdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class SimpleService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var timerObject: Timer? = null
    private var counter = 0
    private var isCounterRunning = false
    private val CHANNEL_ID = "MusicChannel"
    private var mediaSession: MediaSessionCompat? = null
    private var currentFileName: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    resumeMusic()
                }

                override fun onPause() {
                    pauseMusic()
                }

                override fun onStop() {
                    stopMusic()
                }
            })
            isActive = true
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("SimpleService", "onStartCommand action: $action")
        if (action != null && action.startsWith("ACTION_")) {
            Toast.makeText(this, "Action: $action", Toast.LENGTH_SHORT).show()
        }

        when (action) {
            "ACTION_PLAY" -> {
                val fileName = intent.getStringExtra("EXTRA_FILE_NAME") ?: "Unknown Song"
                currentFileName = fileName
                intent.data?.let { playTrack(it, fileName) }
            }
            "ACTION_PAUSE" -> {
                pauseMusic()
            }
            "ACTION_RESUME" -> {
                resumeMusic()
            }
            "ACTION_STOP_MUSIC" -> {
                stopMusic()
            }
            "ACTION_START_COUNTER" -> {
                startCounter()
            }
            "ACTION_STOP_COUNTER" -> {
                stopCounter()
            }
        }

        return START_STICKY
    }

    private fun startCounter() {
        if (isCounterRunning) return

        isCounterRunning = true
        counter = 0
        timerObject = Timer()
        timerObject?.schedule(object : TimerTask() {
            override fun run() {
                counter++
                Log.d("Service Log", "Counter Value: $counter")
            }
        }, 0, 1000)
        Log.d("Service Log", "Counter Started")
    }

    private fun stopCounter() {
        timerObject?.cancel()
        timerObject = null
        isCounterRunning = false
        Log.d("Service Log", "Counter Stopped")
    }

    private fun playTrack(uri: Uri, fileName: String) {
        stopMusic(false) // Don't stop foreground yet if we are changing track
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@SimpleService, uri)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    showNotification(fileName, true)
                    Toast.makeText(this@SimpleService, "Playing: $fileName", Toast.LENGTH_SHORT).show()
                }
                setOnCompletionListener {
                    updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
                    showNotification(fileName, false)
                }
            }
        } catch (e: Exception) {
            Log.e("SimpleService", "Error playing music", e)
        }
    }

    private fun pauseMusic() {
        Log.d("SimpleService", "pauseMusic() called. mediaPlayer: $mediaPlayer")
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
                showNotification(currentFileName, false)
                Log.d("SimpleService", "Music Paused")
                Toast.makeText(this, "Music Paused", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SimpleService", "pauseMusic: Not playing, so not pausing")
            }
        } ?: Log.d("SimpleService", "pauseMusic: mediaPlayer is null")
    }

    private fun resumeMusic() {
        Log.d("SimpleService", "resumeMusic() called. mediaPlayer: $mediaPlayer")
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                showNotification(currentFileName, true)
                Log.d("SimpleService", "Music Resumed")
                Toast.makeText(this, "Music Resumed", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SimpleService", "resumeMusic: Already playing")
            }
        } ?: Log.d("SimpleService", "resumeMusic: mediaPlayer is null")
    }

    private fun updatePlaybackState(state: Int) {
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
            .build()
        mediaSession?.setPlaybackState(playbackState)
    }

    private fun showNotification(fileName: String, isPlaying: Boolean) {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            val pauseIntent = Intent(this, SimpleService::class.java).apply { action = "ACTION_PAUSE" }
            val pausePendingIntent = PendingIntent.getService(
                this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)
        } else {
            val resumeIntent = Intent(this, SimpleService::class.java).apply { action = "ACTION_RESUME" }
            val resumePendingIntent = PendingIntent.getService(
                this, 2, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", resumePendingIntent)
        }

        val stopIntent = Intent(this, SimpleService::class.java).apply { action = "ACTION_STOP_MUSIC" }
        val stopPendingIntent = PendingIntent.getService(
            this, 3, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Now playing: $fileName")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseAction)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1)
                    .setMediaSession(mediaSession?.sessionToken)
            )
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }

    private fun stopMusic(stopForeground: Boolean = true) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        if (stopForeground) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        Log.d("Service Log", "Music Stopped")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCounter()
        stopMusic()
        mediaSession?.release()
    }
}
