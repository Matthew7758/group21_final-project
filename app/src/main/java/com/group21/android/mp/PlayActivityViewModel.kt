package com.group21.android.mp

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PlayActivityViewModel(application: Application) : AndroidViewModel(application) {
    var songList: MutableList<Song> = mutableListOf<Song>()
    var position: Int = 0
    var songName = ""
}