package com.example.cse438.blackjack.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Score(): Serializable {

    private var win: Long=0
    private var userId: String = ""
    private var username: String = ""
    private var lose: Long=0

    constructor( win: Long, userId: String, username: String, lose: Long): this() {

        this.win = win
        this.userId = userId
        this.username = username
        this.lose = lose
    }


    fun addWin(){
        this.win=this.win+1
    }
    fun addLose(){
        this.lose=this.lose+1
    }

    fun getWin(): Long {
        return this.win
    }
    fun getLose(): Long {
        return this.lose
    }

    fun getUserId(): String {
        return this.userId
    }

    fun getUsername(): String {
        return this.username
    }

//    fun getDate(): Timestamp {
//        return date
//    }
}