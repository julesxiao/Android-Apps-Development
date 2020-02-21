package com.example.cse438.blackjack

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MusicService: Service() {
    private lateinit var mediaPlayer:MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(this, R.raw.song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
       // return super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
        }
    }
}