package com.example.musicappservice.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicappservice.MainActivity
import com.example.musicappservice.OnSendStatusSong
import com.example.musicappservice.R
import com.example.view_task2.SongRecyclerViewAdapter

class ListSongFragment : Fragment(), SongRecyclerViewAdapter.OnItemClickListener {

    var recyclerView: RecyclerView? = null
    var songRecyclerViewAdapter = SongRecyclerViewAdapter(ArrayList(), this)
    var onSenStatusSong: OnSendStatusSong? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSenStatusSong = activity as OnSendStatusSong
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_listsong, container, false)

        recyclerView = view.findViewById(R.id.recylerViewSong)
        songRecyclerViewAdapter = SongRecyclerViewAdapter(MainActivity().listSong, this)
        val linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView?.layoutManager = linearLayoutManager

        recyclerView?.adapter = songRecyclerViewAdapter



        return view
    }


    override fun onItemClick(position: Int) {
        super.onItemClick(position)
        onSenStatusSong!!.sendDataSong(position)
    }


}