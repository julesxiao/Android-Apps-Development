package com.example.photosharingxjq.model

import java.io.Serializable

class User(): Serializable {

    private var first_name: String = ""
    private var last_name: String = ""
    private var email: String = ""
    private var username: String = ""
    private var userId: String = ""

    constructor(first_name: String, last_name: String, email: String, username: String, userId: String): this() {
        this.first_name = first_name
        this.last_name = last_name
        this.email = email
        this.username = username
        this.userId = userId
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

    fun getUserId(): String {
        return this.userId
    }
}