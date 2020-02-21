package com.example.cse438.blackjack.model

import com.google.firebase.Timestamp
import java.io.Serializable

class Money(): Serializable {

    private var money: Int=0
    private var userId: String = ""
    private var username: String = ""

    constructor( money: Int, userId: String, username: String): this() {

        this.money = money
        this.userId = userId
        this.username = username
    }


    fun addWin(bet: Int){
        this.money=this.money + bet
    }
    fun addLose(bet: Int){
        this.money=this.money - bet
    }

    fun getMoney(): Int {
        return this.money
    }

    fun getUserId(): String {
        return this.userId
    }

    fun getUsername(): String {
        return this.username
    }
}