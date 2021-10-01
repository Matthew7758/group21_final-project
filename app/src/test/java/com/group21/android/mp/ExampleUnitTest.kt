package com.group21.android.mp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModelProvider
import org.junit.Test
import com.group21.android.mp.MainActivity
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
class SongTest {
    @Test
    fun songSetCorrect() {
        val song = Song("SongName", "Kidz Bop 6969", "Obama", "/emulated/storage/0/Music/test.mp3","60000")
        assertEquals(song, Song("SongName", "Kidz Bop 6969", "Obama", "/emulated/storage/0/Music/test.mp3","60000"))
    }
}

