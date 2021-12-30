package com.example.musicappservice

//Main will handle(override) methods (because it's an intermediary to transfer data between fragments,as I understand it )
interface OnSendStatusSong {
    //send position of item clicked to update
    fun sendDataSong(index: Int?)
    //receive action next, pre or pause/resume
    fun receiveAction(i: Int?)
    //main receive current time of mediaPlay, in order to send to Service
    fun receiveCurrentTime (t : Int?)


}