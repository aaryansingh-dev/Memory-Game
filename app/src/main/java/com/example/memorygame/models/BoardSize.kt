package com.example.memorygame.models

import android.os.Parcelable


enum class BoardSize(val numCards: Int){

    EASY(numCards = 8),
    MEDIUM(numCards = 12),
    HARD(numCards = 18);


    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 2
            HARD -> 3
        }
    }

    fun getHeight(): Int
    {
        return numCards / getWidth()
    }

    fun getNumPairs(): Int
    {
        return numCards / 2
    }
}
