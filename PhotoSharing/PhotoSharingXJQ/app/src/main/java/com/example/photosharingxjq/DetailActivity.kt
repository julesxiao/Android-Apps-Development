package com.example.photosharingxjq

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.photosharingxjq.model.Picinfo
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import java.util.HashMap
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.view.View
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {
    var countUpdate:Int = 0
    var likeadd:Int = 0
    // id of the current user
    private lateinit var pathGet: String
    internal var storage:FirebaseStorage? = null
    internal var storageReference:StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        val userId2 = App.firebaseAuth?.currentUser?.uid
        val locationGet: String = intent.getStringExtra("USER_LOCATOIN")
        val usernameGet: String = intent.getStringExtra("USER_NAME")
        val dateGet:String = intent.getStringExtra("USER_DATE")
        //userId corresponding to the user who took the picture
        val picUserId:String = intent.getStringExtra("USER_ID")
        Log.e("XJQTEST picUserId", picUserId)
        Log.e("XJQTEST userId2", userId2)
        pathGet = intent.getStringExtra("USER_PATH")
        val countGet:String = intent.getStringExtra("USER_COUNT")

        countUpdate = countGet.toInt()
        locationText.text = "Location: " + locationGet
        usernameText.text = "Username: " + usernameGet
        //countDisplay how many people liked the picture
        likeadd = 0
        updateCount()
        //end display
        dateText.text = "Date: " + dateGet
        Glide.with(this /* context */)
            .load(pathGet)
            .into(imageView)

        // TODO: delete photos
        deleteButton.setOnClickListener(){
            //see if the user has the permission to delete the current picture
            if(userId2 == picUserId){
                //delete coresponding photo info
                deletePhoto()
                //Add snackbar
                var snackbar = Snackbar.make(root_layout,"Deletion success! Return to map.", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, MapsActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()
                //end
            }else{
                val toast=Toast.makeText(applicationContext,"No permission",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    // for press like and add reviews
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
        //if (countUpdate == 0) {
        menu?.getItem(0)?.icon = getDrawable(R.drawable.heart_unliked)
//        }
//        else{
//            menu?.getItem(0)?.icon = getDrawable(R.drawable.heart_liked)
//        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            // press like
            R.id.action_favorite -> {
//                if (countUpdate > 0) {
//                    item.icon = getDrawable(R.drawable.heart_liked)
//                    likeadd = 1
//                }
//                else {
//                    item.icon = getDrawable(R.drawable.heart_unliked)
//                    likeadd = 0
//                }

                item.icon = getDrawable(R.drawable.heart_liked)
                likeadd = 1
                updateCount()

                return false
            }
//            R.id.action_review -> {
//                App.openReviewDialog(this, this.product.getId())
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateCount(){
        val userId = App.firebaseAuth?.currentUser?.uid

        if(userId != null){
            val db = FirebaseFirestore.getInstance()
            db.collection("users").get().addOnCompleteListener{intent->
                if(intent.isSuccessful) {
                    val data = intent.result!!
                    //Log.e("XJQTEST", data.toString())
                    if (data != null) {
                        for (eachUser in data) {
                            var useridcurrent: String = eachUser.get("user_id") as String
                            var newPicturesInfo = ArrayList<Picinfo>()
                            val picData = eachUser.get("pictures") as? ArrayList<HashMap<String, Any>>
                            if (picData != null && picData.size > 0) {
                                for (eachPic in picData) {
                                    //countEach = picData.indexOf(eachPic)
                                    if (eachPic != null) {
                                        var pathPic = eachPic.get("photoPath") as String
                                        var datePic = eachPic.get("photoDate") as String
                                        var locationPic = eachPic.get("photoLoc") as HashMap<String, Any>
                                        var latitudePic = locationPic.get("latitude") as Double
                                        var longitutePic = locationPic.get("longitude") as Double
                                        var countlike = eachPic.get("count") as Long
                                        if (pathGet == pathPic) {
                                            if (likeadd == 1) {
                                                countlike = countlike + 1
                                            }
                                            //                                else{
                                            //                                    countlike = countlike -1
                                            //                                }
                                            countDisplay.text = countlike.toString()
                                        }

                                        newPicturesInfo.add(
                                            Picinfo(
                                                useridcurrent,
                                                datePic,
                                                pathPic,
                                                LatLng(latitudePic.toDouble(), longitutePic.toDouble()),
                                                countlike.toInt()
                                            )
                                        )
                                    }
                                }
                            }
                            val map2 = hashMapOf(
                                Pair("first_name", eachUser.get("first_name")),
                                Pair("last_name", eachUser.get("last_name")),
                                Pair("email", eachUser.get("email")),
                                Pair("username", eachUser.get("username")),
                                Pair("pictures", newPicturesInfo),
                                Pair("user_id", useridcurrent)
                            )
                            //Log.e("XJQTEST",useridcurrent.toString())
                            db.collection("users").document(useridcurrent).set(map2)
                        }
                    }
                }
            }
        }
    }

    fun deletePhoto(){
        val userId = App.firebaseAuth?.currentUser?.uid

        if(userId != null){
            val db = FirebaseFirestore.getInstance()
            db.document("users/$userId").get().addOnCompleteListener{intent->
                if(intent.isSuccessful) {
                    val eachUser = intent.result!!
                    //Log.e("XJQTEST", data.toString())
                    var newPicturesInfo = ArrayList<Picinfo>()
                    val picData = eachUser.get("pictures") as? ArrayList<HashMap<String, Any>>
                    if (picData != null && picData.size > 0) {
                        for (eachPic in picData) {
                            //countEach = picData.indexOf(eachPic)
                            if (eachPic != null) {
                                var pathPic = eachPic.get("photoPath") as String
                                var datePic = eachPic.get("photoDate") as String
                                var locationPic = eachPic.get("photoLoc") as HashMap<String, Any>
                                var latitudePic = locationPic.get("latitude") as Double
                                var longitutePic = locationPic.get("longitude") as Double
                                var countlike = eachPic.get("count") as Long
                                if(pathGet == pathPic){

                                }
                                else{
                                    newPicturesInfo.add(Picinfo(
                                        userId,
                                        datePic,
                                        pathPic,
                                        LatLng(latitudePic.toDouble(), longitutePic.toDouble()),
                                        countlike.toInt()
                                    ))
                                }
                            }
                        }
                    }
                    val map2 = hashMapOf(
                        Pair("first_name", eachUser.get("first_name")),
                        Pair("last_name", eachUser.get("last_name")),
                        Pair("email", eachUser.get("email")),
                        Pair("username", eachUser.get("username")),
                        Pair("pictures", newPicturesInfo),
                        Pair("user_id", eachUser.get("user_id"))
                    )
                    var doc = eachUser.get("user_id") as String
                    db.collection("users").document(doc).set(map2)
                }
            }
        }
    }
}
