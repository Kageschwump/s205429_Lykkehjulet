package com.example.lykkehjulet1


/**
 * This enum class holds all of the words that the user can be asked to guess
 */

enum class WordList(val cat: List<String>){
    Mammals(listOf("beaver", "elephant", "kangaroo")),
    Birds(listOf("albatross", "vulture", "woodpecker")),
    Tools(listOf("hammer", "chainsaw", "wheelbarrow"))
}

