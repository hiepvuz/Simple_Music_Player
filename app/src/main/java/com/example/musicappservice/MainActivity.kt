package com.example.musicappservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.musicappservice.Fragment.ListSongFragment
import com.example.musicappservice.Fragment.PlayingFragment
import com.example.musicappservice.Model.Song
import com.example.musicappservice.Service.MediaForegroundService
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity(), OnSendStatusSong {

    private val TAG: String = this.javaClass.name

    var fragmentTransaction: FragmentTransaction? = null
    var listSong: ArrayList<Song> = arrayListOf()
    var listSongFragment: Fragment? = null
    var playFragment: Fragment? = null
    var fragmentManager: FragmentManager? = null


    init {
        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem1",
                "Tony Britten",
                R.drawable.sontung,
                R.raw.pika
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem2",
                "Tony Britten",
                R.drawable.c1,
                R.raw.ucl_song
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem3",
                "Tony Britten",
                R.drawable.c1,
                R.raw.ucl_song
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem4",
                "Tony Britten",
                R.drawable.sontung,
                R.raw.pika
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem5",
                "Tony Britten",
                R.drawable.c1,
                R.raw.ucl_song
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem6",
                "Tony Britten",
                R.drawable.c1,
                R.raw.ucl_song
            )
        )

        listSong.add(
            Song(
                0,
                "UEFA Champion League Anthem7",
                "Tony Britten",
                R.drawable.sontung,
                R.raw.pika
            )
        )
    }

    var songPlaying: Song? = null
    var positionPlaying: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listSongFragment = ListSongFragment()

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()

        fragmentTransaction!!.add(R.id.fragment_container, listSongFragment as ListSongFragment)
            .addToBackStack("x")
        fragmentTransaction!!.commit()
    }

    @ExperimentalTime
    override fun sendDataSong(index: Int?) {
        if (intent != null) {
            stopService(intent)
        }
        playFragment = PlayingFragment()
        (playFragment as PlayingFragment).receiveDataSong(listSong[index!!])


        Log.d(TAG, "sendDataSong: ${fragmentManager!!.backStackEntryCount}")

            //ngoài list fragment ra, backstak fragment đang có 1 fragment playing đang chạy bài hát
        if (fragmentManager!!.backStackEntryCount > 1) {
            //pop ra để đè thêm fragment của bài mới chọn
            fragmentManager!!.popBackStack()
            fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction!!.add(R.id.fragment_container, playFragment as PlayingFragment)
                .addToBackStack("x")

            fragmentTransaction!!.commit()
        } else {
            fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction!!.add(R.id.fragment_container, playFragment as PlayingFragment)
                .addToBackStack("x")
            fragmentTransaction!!.commit()
        }

        intent = Intent(this, MediaForegroundService::class.java)
        var bundle = Bundle()
        positionPlaying = index
        songPlaying = listSong[index]

        bundle.putSerializable("song", songPlaying)
        intent.action = ACTIVITY_SERVICE
        intent.putExtra("song_played", bundle)

        ContextCompat.startForegroundService(this, intent)
    }

    //nhận action là ấn nút nào (do playingFragment quản lí)
    override fun receiveAction(typeAction: Int?) {
        Log.d(TAG, "receiveAction: $positionPlaying")
        when (typeAction) {
            //pause
            0 -> {
                intent = Intent(this, MediaForegroundService::class.java)
                var newBundle = Bundle()
                newBundle.putInt("actionChange", 0)
                intent.putExtra("song_played", newBundle)
                ContextCompat.startForegroundService(this, intent)
            }

            //bài sau
            1 -> {
                intent = Intent(this, MediaForegroundService::class.java)
                var newBundle = Bundle()
                if (positionPlaying!! < listSong.size - 2)
                    newBundle.putSerializable("song", listSong[positionPlaying!! + 1])
                else newBundle.putSerializable("song", listSong[0])

                positionPlaying = positionPlaying!! + 1
                intent.putExtra("song_played", newBundle)
                ContextCompat.startForegroundService(this, intent)
            }

            //bài trước
            -1 -> {
                intent = Intent(this, MediaForegroundService::class.java)
                var newBundle = Bundle()
                if (positionPlaying!! > 0)
                    newBundle.putSerializable("song", listSong[positionPlaying!! - 1])
                else {
                    newBundle.putSerializable("song", listSong[listSong.size - 1])
                }
                positionPlaying = positionPlaying!! - 1
                intent.putExtra("song_played", newBundle)
                ContextCompat.startForegroundService(this, intent)
            }

        }
    }

    //nhận progress hiện tại của seekbar (playingFragment quản lí) để gửi cho service điều chỉnh nhạc
    override fun receiveCurrentTime(progress: Int?) {
        intent = Intent(this, MediaForegroundService::class.java)
        var newBundle = Bundle()
        newBundle.putInt("progress", progress!!)
        intent.putExtra("sbChange", newBundle)
        ContextCompat.startForegroundService(this, intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intent)
        Log.d(TAG, "onDestroy: activity")
    }


}


