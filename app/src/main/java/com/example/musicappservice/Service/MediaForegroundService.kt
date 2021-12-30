package com.example.musicappservice.Service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.musicappservice.MainActivity
import com.example.musicappservice.Model.Song
import com.example.musicappservice.R


class MediaForegroundService : Service() {
    private val TAG: String = this.javaClass.name

    private val CHANNEL_ID = "ForegroundServiceMusicChannel"
    private val CHANNEL_NAME = "Foreground Service Music"
    private var mediaPlayer: MediaPlayer? = null
    var isRunning: Boolean? = null
    var isPlaying: Boolean = false

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    fun startMusic(song: Song) {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer = MediaPlayer.create(applicationContext, song.songSource)
        mediaPlayer!!.start()
        isPlaying = true
        if (!(mediaPlayer!!.isPlaying)) {
            isPlaying = false
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(TAG, "onStart: here")
        super.onStart(intent, startId)
    }
    override fun onCreate() {
        Log.d(TAG, "onCreate: here")
        super.onCreate()
        isRunning = true
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ShowToast")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: here")
        val bundle: Bundle? = intent!!.getBundleExtra("song_played")
        createNotificationChannel()

        val notificationIntent = Intent(this, MediaForegroundService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.drawable.c1)
            //giao diện notify đơn giản, báo là đang phát nhạc
            .setContentText("PLaying Music ...")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        Thread {
            if (bundle?.getSerializable("song") != null) {
                val song: Song = bundle.getSerializable("song") as Song
                startMusic(song)
            }

            if (mediaPlayer == null) {
                stopSelf()
            }

        }.start()

        //int action gửi đến = 0 là pause/resume
        if (bundle?.getInt("actionChange", 3) == 0) {
            if (isPlaying && mediaPlayer != null) {
                mediaPlayer!!.pause()
                isPlaying = false

            } else {
                if (!isPlaying) {

                    mediaPlayer!!.start()
                    isPlaying = true
                } else {
                    Toast.makeText(applicationContext, "Choose 1 song", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val bundleSeekBar: Bundle? = intent.getBundleExtra("sbChange")

        //khi seek bar của playing fragment thay đổi (bundleSeekBar != null)
        if (bundleSeekBar != null) {
            val currentTimeSong = bundleSeekBar.getInt("progress", 0)
            if (mediaPlayer != null) {

                Toast.makeText(baseContext, "Song progress has changed", Toast.LENGTH_SHORT)
                    .show()
                //log show thời gian hiện tại của bài hát, chưa seek to được, nhưng đã hiện được Toast
                Log.d(TAG, "onStartCommand: $currentTimeSong now")
                //mediaPlayer!!.seekTo(currentTimeSong)

            }
        }
        Log.d(TAG, "onStartCommand: $startId")
        return START_NOT_STICKY

    }

    override fun onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            isPlaying = false
            mediaPlayer = null
        }
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


}