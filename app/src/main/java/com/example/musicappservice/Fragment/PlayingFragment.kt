package com.example.musicappservice.Fragment

import android.content.Context

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.musicappservice.MainActivity
import com.example.musicappservice.Model.Song
import com.example.musicappservice.OnSendStatusSong
import com.example.musicappservice.R
import android.widget.SeekBar.OnSeekBarChangeListener as OnSeekBarChangeListener

class PlayingFragment : Fragment() {

    private val TAG: String = this.javaClass.name
    var receiveSong: Song? = null
    var onSendStatusSong: OnSendStatusSong? = null
    var seekBarPlayer: SeekBar? = null

    private var durationSong: Int? = 0
    private var currentPos = 0
    var mediaPlayer: MediaPlayer? = null
    var runnable: Runnable? = null
    private var handler: Handler? = null
    private var isPause: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSendStatusSong = activity as OnSendStatusSong
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play, container, false)
        Log.d(TAG, "onCreateView: now")
        mediaPlayer = MediaPlayer.create(
            (activity as MainActivity).applicationContext,
            receiveSong!!.songSource
        )

        seekBarPlayer = view.findViewById(R.id.sb)

        Log.d(TAG, "onCreateView: $durationSong")
        //ấn previous
        view.findViewById<ImageView>(R.id.pre).setOnClickListener {
            onSendStatusSong!!.receiveAction(-1)
        }
        //ấn pause
        view.findViewById<ImageView>(R.id.pause).setOnClickListener {
            isPause = !isPause
            if (!isPause) {
                updateSeekBar()
            }
            onSendStatusSong!!.receiveAction(0)
        }
        //ấn next
        view.findViewById<ImageView>(R.id.next).setOnClickListener {
            onSendStatusSong!!.receiveAction(1)
        }

        Log.d(TAG, "onResume: now")
        durationSong =
            (mediaPlayer!!.duration) / 1000
        seekBarPlayer!!.max = durationSong!!

        //khi chọn bài khác bài, fragment này chạy lại vào onResume, các biến, function sau cần set lại
        currentPos = 0
        handler = Handler()
        isPause = false
        updateSeekBar()

        seekBarPlayer!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, user: Boolean) {
                //seekbar change bởi người dùng
                if (user) {
                    currentPos = progress
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                onSendStatusSong!!.receiveCurrentTime(p0!!.progress)
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        return view
    }

    private fun updateSeekBar() {
        if (currentPos <= durationSong!! && !isPause) {
            seekBarPlayer!!.progress = currentPos
            currentPos += 1
            runnable = Runnable {
                kotlin.run {
                    updateSeekBar()
                    Log.d(TAG, "updateSeekBar: $currentPos")
                }
            }
            handler!!.postDelayed(runnable!!, 1000)
        }
    }

    fun receiveDataSong(song: Song?) {
        receiveSong = song
    }

}