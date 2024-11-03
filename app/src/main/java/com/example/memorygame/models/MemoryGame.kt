package com.example.memorygame.models

import android.util.Log
import com.example.memorygame.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize)
{


    val cards : List<MemoryCard>
    var numPairsFound = 0
    private var selectedCard: Int? = null
    var numCardFlips = 0


    init {
        val shuffledIcons = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomImages = (shuffledIcons + shuffledIcons).shuffled()
        cards = randomImages.map { MemoryCard(it) }
    }

    fun flipCard(position: Int): Boolean {
        var card = cards[position]
        numCardFlips++
        //  3 states
        // 0 cards flipped previously
        // 1 card flipped previously ->  selectedCard
        // 2 card flipped previously

        var foundMatch = false

        if(selectedCard == null)
        {
            selectedCard = position
            restoreCards()
        }
        else
        {
            foundMatch = ifCardsMatched(selectedCard!!, position)
            if(foundMatch)
            {
                updateCardState(cards[selectedCard!!], cards[position])
            }
            selectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun ifCardsMatched(pos1: Int, pos2: Int): Boolean {
        return cards[pos1].identifier == cards[pos2].identifier
    }

    private fun updateCardState(card1: MemoryCard, card2: MemoryCard)
    {
        card1.isMatched = true
        card2.isMatched = true
        numPairsFound++
    }

    private fun restoreCards() {
        cards.forEach{card ->
            if(!card.isMatched){
            card.isFaceUp = false
            }
        }
    }

    fun haveWon(): Boolean {
        return (numPairsFound == boardSize.getNumPairs())
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMove(): Int
    {
        return numCardFlips/2
    }

}