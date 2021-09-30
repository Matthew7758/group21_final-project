package com.group21.android.mp

import org.junit.Test

import org.junit.Assert.*

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
        val song: Song = Song("SongName", "Kidz Bop 6969", "Obama", "/emulated/storage/0/Music/test.mp3","60000")
        assertEquals(song, Song("SongName", "Kidz Bop 6969", "Obama", "/emulated/storage/0/Music/test.mp3","60000"))
    }
}