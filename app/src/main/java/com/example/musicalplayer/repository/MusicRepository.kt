package com.example.musicalplayer.repository

import androidx.annotation.RawRes
import com.example.musicalplayer.R


class MusicRepository {

    val musicRep = mutableListOf<Track>(
        Track("dd",R.raw.q),
        Track("tdd",R.raw.w),
        Track("rysas_of_heart",R.raw.e),

    )

    private val maxIndex: Int = musicRep.size
    private var currentItemIndex = 0

    fun getCurrent(): Track = musicRep[currentItemIndex]


    fun getNext(): Track {
        if (currentItemIndex == maxIndex-1) currentItemIndex = 0 else currentItemIndex++
        return getCurrent()
    }

    fun getPrevious(): Track {
        if (currentItemIndex == 0) currentItemIndex = maxIndex-1 else currentItemIndex--
        return getCurrent()
    }

}

data class Track(
    val musicName : String,
    @RawRes val music: Int
)