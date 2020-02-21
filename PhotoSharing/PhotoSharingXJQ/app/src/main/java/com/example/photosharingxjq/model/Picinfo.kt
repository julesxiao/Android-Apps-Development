package com.example.photosharingxjq.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Picinfo(): Serializable {

    private var userId: String = ""
    private var photoDate:String = ""
    private var photoPath: String = ""
    private var photoLoc: LatLng = LatLng(0.toDouble(),0.toDouble())
    // count how many people pressed like on this photo
    private var count: Int = 0

    constructor( userId: String, photoDate:String, photoPath: String, photoLoc: LatLng, count:Int): this() {
        this.userId = userId
        this.photoDate = photoDate
        this.photoPath = photoPath
        this.photoLoc = photoLoc
        this.count = count
    }

    fun addCount(){
        this.count=this.count+1
    }
    fun lostCount(){
        this.count=this.count+1
    }

    fun getUserId(): String {
        return this.userId
    }


    fun getphotoDate(): String {
        return this.photoDate
    }

    fun getphotoPath(): String {
        return this.photoPath
    }

    fun getphotoLoc(): LatLng {
        return this.photoLoc
    }

    fun getcount(): Int {
        return this.count
    }
}