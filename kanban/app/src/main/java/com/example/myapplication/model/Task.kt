package com.example.myapplication.model

import java.io.Serializable

class Task(): Serializable {


    private var date = ""

    private var title=""


    private var body:String=""
    private var photo : String = ""





    constructor( date:String, title:String, body:String, photo:String): this() {


        this.date=date
        this.title=title
        this.body=body
        this.photo=photo
    }
    fun getDate():String {
        return this.date
    }

    fun getBody(): String{
        return this.body
    }
     fun getTitle():String{
      return this.title
     }
    fun getPhoto():String{
        return this.photo
    }
}

