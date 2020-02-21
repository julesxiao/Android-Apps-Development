package com.example.cse438.blackjack

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cse438.blackjack.model.Money
import com.example.cse438.blackjack.model.Score
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class App {
    companion object {
        var firebaseAuth: FirebaseAuth? = null
//        fun updateScore(context:Context,winorlose:Int, bet:Int){
//        val userId = firebaseAuth?.currentUser?.uid
//
//        if(userId!=null)
//        {
//            val db = FirebaseFirestore.getInstance()
//
//            db.collection("users").document(firebaseAuth!!.currentUser!!.uid).get().addOnCompleteListener { it2 ->
//                if (it2.isSuccessful) {
//                    val userData = it2.result!!
//                    var data2score = userData.get("scores") as? HashMap<String, Any>
//                    var data2 = Score(
//                        0,
//                        firebaseAuth!!.currentUser!!.uid,
//                        userData.get("username") as String,
//                        0)
//                    if (userData.get("scores")!=null) {
//                         data2 = Score(
//                            data2score?.get("win") as Long,
//                            firebaseAuth!!.currentUser!!.uid,
//                            userData.get("username") as String,
//                            data2score?.get("lose") as Long
//                        )
//                    }
//
//                    if (winorlose == 1) {
//                        data2.addWin()
//                    } else {
//                        data2.addLose()
//                    }
//
//                    //money
//                    var datat = userData.get("money") as? Long
//                    var data=datat?.toInt()
//                    //Log.e("userData2", userData.get("money").toString())
//                    //Log.e("Data2", data.toString())
//                    if (userData.get("money") == null) {
//                        data=5000
//                    }
//                    if(data!=null) {
//                        if (winorlose == 1) {
//                            data = data + bet
//                        } else {
//                            data = data - bet
//                        }
//                    }
//                    if(data!! <= 0){
//                        data=5000
//                        val builder = AlertDialog.Builder(this@GameActivity)
//
//                        // Set the alert dialog title
//                        builder.setTitle("App background color")
//
//                        // Display a message on alert dialog
//                        builder.setMessage("Are you want to set the app background color to RED?")
//
//                        // Set a positive button and its click listener on alert dialog
//                        builder.setPositiveButton("YES"){dialog, which ->
//                            // Do something when user press the positive button
//                            Toast.makeText(applicationContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
//
//                            // Change the app background color
//                        }
//
//
//                    }
//
//
//                    val map2 = hashMapOf(
//                        Pair("first_name", userData.get("first_name")),
//                        Pair("last_name", userData.get("last_name")),
//                        Pair("email", userData.get("email")),
//                        Pair("username", userData.get("username")),
//                        Pair("scores", data2),
//                        Pair("money", data)
//                    )
//
//                    db.collection("users").document(firebaseAuth!!.currentUser!!.uid).set(map2)
//                }
//            }
//        }
//    }


//        fun getScoreFromDataBase(context:Context):ArrayList<Long>? {
//            if(firebaseAuth?.currentUser == null){
//                Log.e("Error","currentUser is null" )
//            }
//            else {
//                val userId = firebaseAuth?.currentUser?.uid
//                var res = ArrayList<Long>()
//                Log.e("Error","User is null" )
//                val db = FirebaseFirestore.getInstance()
//
//                db.collection("users").document(userId!!).get().addOnCompleteListener { it2 ->
//                    Log.e("Exception", it2.exception.toString())
//                    if (it2.isSuccessful) {
//                        val userData = it2.result!!
//
//                        var data2score = userData.get("scores") as? HashMap<String, Any>
//                        //Log.e("data2getwin",data2score?.get("win").toString())
//                        //Log.e("data2getlose",data2score?.get("lose").toString())
//                        res.add(data2score?.get("win") as Long)
//                        res.add(data2score?.get("lose") as Long)
//                    }else{
//                        Toast.makeText(context,"Unable to get score",Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                Log.e("Res", res.toString() )
//                return res
//            }
//
//           return null
//        }
}}