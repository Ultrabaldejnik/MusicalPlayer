package com.example.musicalplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.musicalplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: MusicService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this,intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initUI()
    }

    fun initUI() {
        with(binding) {
            previousBtn.setOnClickListener {
                controller.playPrevTrack()
            }

            playBtn.setOnClickListener {
                controller.playMusic()

            }

            pauseBtn.setOnClickListener {
                controller.pauseTrack()
            }

            nextBtn.setOnClickListener {
                controller.playNextTrack()
            }
        }
    }



    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val localBinder = service as MusicService.LocalBinder
            controller = localBinder.getBindServiceInstance()
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
