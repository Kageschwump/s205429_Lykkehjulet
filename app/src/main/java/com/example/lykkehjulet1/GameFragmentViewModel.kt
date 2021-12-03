package com.example.lykkehjulet1

import androidx.lifecycle.ViewModel

/**
 * This viewmodel is used to hold all of the data that is being used by the application.
 *
 */


class GameFragmentViewModel: ViewModel() {

    var user = UserData(0,5)
    var hiddenWord: String? = null
    var word: String? = null
    var guessedLetters = mutableListOf<Char>()

}