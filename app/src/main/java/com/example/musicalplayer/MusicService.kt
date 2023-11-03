package com.example.musicalplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.musicalplayer.repository.MusicRepository
import com.example.musicalplayer.repository.Track
import java.lang.Exception


class MusicService : Service() {

    private val localBinder = LocalBinder()
    private lateinit var mPlayer: MediaPlayer
    private lateinit var musicRepository: MusicRepository

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    //нужно для того, чтобы при нажатии на кнопку не запускалось несколько сервисов
    private var flagPlay = false // это тоже для той же цели

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        musicRepository = MusicRepository()
        mPlayer = MediaPlayer.create(this, musicRepository.getCurrent().music)
        initMediaPlayer()
        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    private fun initMediaPlayer() {
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener {
            try {
                playNextTrack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playMusic() {
        mPlayer.start()
        flagPlay = true

        if (flagPlay){
            initMediaPlayer()
        }

    }

    fun getCurrentMusic(): Track {
        return musicRepository.getCurrent()
    }

    fun playNextTrack() {

        mPlayer.stop()
        if (flagPlay) {
            mPlayer = MediaPlayer.create(this, musicRepository.getNext().music)
            mPlayer.start()
            initMediaPlayer()
        }
    }


    fun playPrevTrack() {
        mPlayer.stop()
        mPlayer = MediaPlayer.create(this, musicRepository.getPrevious().music)
        if (flagPlay) {
            mPlayer.start()
            initMediaPlayer()
        }
    }

    fun pauseTrack() {
            mPlayer.pause()
            flagPlay = false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Playing music")
        .setSmallIcon(R.drawable.ic_music)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(){
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID,notification)
    }




    override fun onDestroy() {
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        fun getBindServiceInstance(): MusicService {
            return this@MusicService
        }
    }

    companion object {
        const val TAG = "MUSIC_BIND_SERVICE"
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 1

    }
}


