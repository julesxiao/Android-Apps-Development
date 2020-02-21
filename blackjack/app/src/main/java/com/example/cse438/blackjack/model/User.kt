package com.example.cse438.blackjack.model

import java.io.Serializable

class User(): Serializable {

    private var first_name: String = ""
    private var last_name: String = ""
    private var email: String = ""
    private var username: String = ""

    constructor(first_name: String, last_name: String, email: String, username: String): this() {
        this.first_name = first_name
        this.last_name = last_name
        this.email = email
        this.username = username
    }

    fun getFirstName(): String {
        return this.first_name
    }

    fun getLastName(): String {
        return this.last_name
    }

    fun getEmail(): String {
        return this.email
    }

    fun getUsername(): String {
        return this.username
    }
}