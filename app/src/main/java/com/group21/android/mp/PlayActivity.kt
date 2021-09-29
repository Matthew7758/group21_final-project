package com.group21.android.mp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.ArrayList

private const val TAG = "PlayActivity"

class PlayActivity : AppCompatActivity() {
    private val playViewModel: PlayActivityViewModel by lazy {
        ViewModelProvider(this).get(PlayActivityViewModel::class.java)
    }
    private lateinit var playButton: ImageButton
    private lateinit var rewindButton: ImageButton
    private lateinit var skipPreviousButton: ImageButton
    private lateinit var fastForwardButton: ImageButton
    private lateinit var skipNextButton: ImageButton
    private lateinit var songNamePlay: TextView
    private lateinit var songImagePlay: ImageView
    private lateinit var songStartTime: TextView
    private lateinit var songEndTime: TextView
    private lateinit var seekbar: SeekBar
    private lateinit var barVisualizer: BarVisualizer
    var mediaPlayer: MediaPlayer? = null
    var updateSeekBar: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        playButton = findViewById(R.id.playButton)
        rewindButton = findViewById(R.id.rewindButton)
        skipPreviousButton = findViewById(R.id.skipPreviousButton)
        fastForwardButton = findViewById(R.id.fastForwardButton)
        skipNextButton = findViewById(R.id.skipNextButton)
        songNamePlay = findViewById(R.id.songNamePlay)
        songImagePlay = findViewById(R.id.songImagePlay)
        songStartTime = findViewById(R.id.songStartTime)
        songEndTime = findViewById(R.id.songEndTime)
        seekbar = findViewById(R.id.seekbar)
        barVisualizer = findViewById(R.id.visualizerPlay)

        //If songs currently playing.
        if (mediaPlayer != null) {
            mediaPlayer!!.start();
            mediaPlayer!!.release();
        }


        //Get intent
        val intent = intent
        val bundle = intent.extras
        val gson = Gson()
        val jsonString = bundle?.getString("songList")
        val listOfSongType: Type = object : TypeToken<List<Song?>?>() {}.type
        val songs : ArrayList<Song> = gson.fromJson(jsonString, listOfSongType)
        for(song in songs)
            playViewModel.songList.add(song)
        //Log.d(TAG, songList.toString())
        playViewModel.songName = bundle?.getString("songName")!!
        playViewModel.position = bundle.getInt("position")


        //Log.d(TAG, "Song name = $songName")
        //Log.d(TAG, "Song position = $position")
        songNamePlay.isSelected = true
        songNamePlay.text = playViewModel.songName
        val songCover: Bitmap? = getAlbumImage(playViewModel.songList[playViewModel.position].path)
        if(songCover!=null)
            songImagePlay.setImageBitmap(songCover)

        //Get media player
        mediaPlayer = MediaPlayer.create(applicationContext, Uri.parse(playViewModel.songList[playViewModel.position].path))
        mediaPlayer?.start()
    }

    private fun getAlbumImage(path: String): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        return if (data != null) BitmapFactory.decodeByteArray(data, 0, data.size) else null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer!!.stop()
    }
}