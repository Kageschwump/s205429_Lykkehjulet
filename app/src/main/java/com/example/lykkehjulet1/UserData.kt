package com.example.lykkehjulet1

/**
 * This dataclass holds makes it possible to initialize a User with 5 lives and 0 cash
 * at the start of the game. It also holds methods for changing that data
 */

data class UserData(var cash: Int, var lives: Int){
    fun addLife(){
        this.lives++
    }

    fun subtractLife(){
        if(this.lives > 0)
        this.lives--
    }

    fun addCash(amount: Int){
        this.cash+=amount
    }

    fun bankrupt(){
        this.cash = 0;
    }
}
