package com.group21.android.mp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit
import android.widget.Toast

import com.squareup.seismic.ShakeDetector

import android.content.Context.SENSOR_SERVICE

import androidx.core.content.ContextCompat.getSystemService

import android.hardware.SensorManager

import android.app.Activity
import android.widget.Toast.LENGTH_SHORT


private const val TAG = "PlayActivity"
class PlayActivity : AppCompatActivity(), ShakeDetector.Listener {
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
    private lateinit var waveVisualizer: WaveVisualizer
    var mediaPlayer: MediaPlayer? = null
    var updateSeekBar: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sd = ShakeDetector(this)
        sd.start(sensorManager)
        setContentView(R.layout.activity_play)
        Toast.makeText(applicationContext, "Shake device to shuffle songs!", LENGTH_SHORT)
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
        waveVisualizer = findViewById(R.id.visualizerPlay)


        //If songs currently playing.
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }


        //Get intent
        val intent = intent
        val bundle = intent.extras
        val gson = Gson()
        val jsonString = bundle?.getString("songList")
        val listOfSongType: Type = object : TypeToken<List<Song?>?>() {}.type
        val songs: ArrayList<Song> = gson.fromJson(jsonString, listOfSongType)
        for (song in songs)
            playViewModel.songList.add(song)
        //Log.d(TAG, songList.toString())
        playViewModel.songName = bundle?.getString("songName")!!
        playViewModel.position = bundle.getInt("position")


        //Log.d(TAG, "Song name = $songName")
        //Log.d(TAG, "Song position = $position")
        songNamePlay.isSelected = true
        songNamePlay.text = playViewModel.songName
        val songCover: Bitmap? = getAlbumImage(playViewModel.songList[playViewModel.position].path)
        if (songCover != null)
            songImagePlay.setImageBitmap(songCover)

        //Get media player
        mediaPlayer = MediaPlayer.create(
            applicationContext,
            Uri.parse(playViewModel.songList[playViewModel.position].path)
        )
        saveFileOp()
        mediaPlayer?.start()
        getSongEndTime()
        showVisualizer()


        //Thread to update the seekBar while playing song
        updateSeekBar = object : Thread() {
            override fun run() {
                val fullTime: Int = mediaPlayer!!.duration
                var currentTime = 0
                while (currentTime < fullTime) {
                    try {
                        sleep(500)
                        currentTime = mediaPlayer!!.currentPosition
                        seekbar.progress = currentTime
                        runOnUiThread { songStartTime.text = getTimeString(currentTime) }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        seekbar.max = mediaPlayer!!.duration
        (updateSeekBar as Thread).start()

        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer!!.seekTo(seekBar.progress)
            }
        })

        playButton.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_50)
                mediaPlayer!!.pause()
            } else {
                playButton.setImageResource(R.drawable.ic_baseline_pause_50)
                mediaPlayer!!.start()
            }
        }

        rewindButton.setOnClickListener {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition - 10000)
        }
        fastForwardButton.setOnClickListener {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition + 10000)
        }
        skipPreviousButton.setOnClickListener {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()

            playViewModel.position = ((playViewModel.position - 1) % playViewModel.songList.size)
            if (playViewModel.position < 0)
                playViewModel.position = playViewModel.songList.size - 1
            val uri: Uri = Uri.parse(playViewModel.songList[playViewModel.position].path)
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            playViewModel.songName = playViewModel.songList[playViewModel.position].name
            songNamePlay.text = playViewModel.songName
            val bitmap: Bitmap? = getAlbumImage(playViewModel.songList[playViewModel.position].path)
            if (bitmap != null)
                songImagePlay.setImageBitmap(bitmap)
            else
                songImagePlay.setImageResource(R.drawable.ic_baseline_music_note_50)
            saveFileOp()
            mediaPlayer!!.start()
            showVisualizer()
        }

        skipNextButton.setOnClickListener {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()

            playViewModel.position = ((playViewModel.position + 1) % playViewModel.songList.size)
            if (playViewModel.position > playViewModel.songList.size)
                playViewModel.position = 0
            val uri: Uri = Uri.parse(playViewModel.songList[playViewModel.position].path)
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            playViewModel.songName = playViewModel.songList[playViewModel.position].name
            songNamePlay.text = playViewModel.songName
            val bitmap: Bitmap? = getAlbumImage(playViewModel.songList[playViewModel.position].path)
            if (bitmap != null)
                songImagePlay.setImageBitmap(bitmap)
            else
                songImagePlay.setImageResource(R.drawable.ic_baseline_music_note_50)
            saveFileOp()
            mediaPlayer!!.start()
            showVisualizer()
        }
    }
    //Takes accelerometer data and shuffles songs if device is shook.
    override fun hearShake() {
        playViewModel.songList.shuffle()
        playViewModel.songList.shuffle()
        playViewModel.songList.shuffle()
        Toast.makeText(this, "Songs shuffled!", LENGTH_SHORT).show()
    }

    private fun saveFileOp() {
        val filename = "save.txt"
        val file = File(applicationContext.filesDir, filename)
        if (file.exists()) {
            file.delete()
            applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(playViewModel.songList[playViewModel.position].path.toByteArray())
            }
        } else {
            applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(playViewModel.songList[playViewModel.position].path.toByteArray())
            }
        }
    }

    private fun showVisualizer() {
        val id = mediaPlayer!!.audioSessionId
        if (id != -1)
            waveVisualizer.setAudioSessionId(id)

    }

    private fun getAlbumImage(path: String): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        return if (data != null) BitmapFactory.decodeByteArray(data, 0, data.size) else null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val filename = "save.txt"
        val file = File(applicationContext.filesDir, filename)
        if (file.exists())
            file.delete()
        mediaPlayer!!.stop()
    }

    private fun getSongEndTime() {
        songEndTime.text = getTimeString(mediaPlayer!!.duration)
    }

    private fun getTimeString(time: Int): String {
        val millis: Long = time.toLong()
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        waveVisualizer.release()
    }
}
