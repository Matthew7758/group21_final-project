package com.group21.android.mp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.group21.android.mp", appContext.packageName)
    }
    private fun getAlbumImage(path: String): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val data = mmr.embeddedPicture
        return if (data != null) BitmapFactory.decodeByteArray(data, 0, data.size) else null
    }
    @Test
    fun getImageTest() {
        val bitmap = getAlbumImage("/storage/emulated/0/Music/test.mp3")
        assertNull(bitmap)
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
    @Test
    fun getTimeTest() {
        assertEquals("01:23:42", getTimeString(5022000))
    }
    private var songList: MutableList<Song> = ArrayList()
    private fun getAllAudioFromDevice(context: Context) {
        val tempAudioList: MutableList<Song> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DURATION
        )
        // if want fetch all files
        val mimeType1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
        val mimeType2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("m4a")
        val mimeType3 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("flac")
        val mimeType4 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("aac")
        val mimeType5 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("wav")
        val mimeType6 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ogg")
        val selectionMimeType =
            MediaStore.Audio.AudioColumns.MIME_TYPE + "=? OR " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?" + "=? OR " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?" + "=? OR " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?" + "=? OR " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?" + "=? OR " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?"
        val selectionArgsMp3 =
            arrayOf(mimeType1, mimeType2, mimeType3, mimeType4, mimeType5, mimeType6)


        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            selectionMimeType,
            selectionArgsMp3,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val audioModel = Song()
                val path: String = cursor.getString(0)
                //Log.d(TAG, path)
                val name: String = cursor.getString(1)
                val album: String = cursor.getString(2)
                val artist: String = cursor.getString(3)
                val duration: String = cursor.getString(4)
                audioModel.name = name
                audioModel.album = album
                audioModel.artist = artist
                audioModel.path = path
                audioModel.duration = duration
                tempAudioList.add(audioModel)
            }
            cursor.close()
        }
        songList = tempAudioList
        return
    }
    @Test
    fun getAudioTest() {
        getAllAudioFromDevice(instrumentationContext)
        assertNotNull(songList)
    }
    @Test
    fun playViewModelTest() {
        val pvm = PlayActivityViewModel(Application())
        val songList : MutableList<Song> = emptyList<Song>().toMutableList()
        songList.add(Song("Song1","Album 1","","/storage/emulated/0/Music/test1.mp3"))
        songList.add(Song("Song2","Album 2","","/storage/emulated/0/Music/test2.mp3"))
        songList.add(Song("Song3","Album 3","","/storage/emulated/0/Music/test3.mp3"))
        songList.add(Song("Song4","Album 4","","/storage/emulated/0/Music/test4.mp3"))
        pvm.songList = songList
        assertEquals(songList,pvm.songList)
    }
    @Test
    fun playViewModelTest2() {
        val pvm = PlayActivityViewModel(Application())
        val songList : MutableList<Song> = emptyList<Song>().toMutableList()
        songList.add(Song("Song1","Album 1","","/storage/emulated/0/Music/test1.mp3"))
        songList.add(Song("Song2","Album 2","","/storage/emulated/0/Music/test2.mp3"))
        songList.add(Song("Song3","Album 3","","/storage/emulated/0/Music/test3.mp3"))
        songList.add(Song("Song4","Album 4","","/storage/emulated/0/Music/test4.mp3"))
        pvm.songList = songList
        pvm.position=1
        pvm.songName="Song2"

        assertEquals("Song2",pvm.songList[pvm.position].name)
    }
    @Test
    fun playViewModelTest3() {
        val pvm = PlayActivityViewModel(Application())
        val songList : MutableList<Song> = emptyList<Song>().toMutableList()
        songList.add(Song("Song1","Album 1","","/storage/emulated/0/Music/test1.mp3"))
        songList.add(Song("Song2","Album 2","","/storage/emulated/0/Music/test2.mp3"))
        songList.add(Song("Song3","Album 3","","/storage/emulated/0/Music/test3.mp3"))
        songList.add(Song("Song4","Album 4","","/storage/emulated/0/Music/test4.mp3"))
        pvm.songList = songList
        pvm.position=1
        pvm.songName="Song2"
        assertEquals("Album 2",pvm.songList[pvm.position].album)
        assertEquals("Song2",pvm.songList[pvm.position].name)
    }

}
