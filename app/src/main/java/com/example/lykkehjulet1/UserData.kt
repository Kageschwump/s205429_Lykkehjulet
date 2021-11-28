package com.example.lykkehjulet1

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
