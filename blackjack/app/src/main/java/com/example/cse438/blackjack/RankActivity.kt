package com.example.cse438.blackjack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.cse438.blackjack.adapter.AccountPagerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_rank.*
import java.util.HashMap

class RankActivity : AppCompatActivity() {
//    var user=ArrayList<String>()
    //var points=ArrayList<Double>()
    var uwithp = HashMap<Double,String>()
    val foodCalorieList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        backButton.setOnClickListener (){
            //Log.e("playbutton","work")
            val intent = Intent(this,MainActivity::class.java)

            val intent2 = Intent(this, MusicService::class.java)
            stopService(intent2)

            startActivity(intent)
        }

        if(App.firebaseAuth?.currentUser == null){
            Log.e("Error","currentUser is null" )
        }
        else {
            val userId = App.firebaseAuth?.currentUser?.uid

            val db = FirebaseFirestore.getInstance()

            db.collection("users").get().addOnCompleteListener { it2 ->
                //Log.e("it2", it2.result.toString())
                if(it2.isSuccessful){
                val usersData = it2.result!!

                //Log.e("list",list.get(0).toString())
                for (i in usersData){
                    var score = i.get("scores") as? HashMap<String, Any>
                    var num= usersData.indexOf(i)
                    Log.e("it2 result",score.toString() )
                    if(score!=null) {
                        var username = score.get("username") as String
                        //user.add(username.toString())
                        var win = score.get("win") as Long
                        var lose=score.get("lose") as Long
                        if(win!=null&&lose!=null) {
                            var point=1.toDouble();
                            if(!lose.equals(0.toLong())){
                                point = win.toDouble() / lose.toDouble()
                            }
//                            else{
//                                point=1.toDouble();
//                            }
                            uwithp.put(point,username)


                        }
                    }
                    if(num==usersData.size()-1){
                        var count=1

                        for((k,v) in uwithp.toSortedMap(reverseOrder())){

                            Log.e("Error", "Layout working ?" )
                            foodCalorieList.add("No."+count +"  "+v)
                            count++
                            val adapter = ArrayAdapter(this, R.layout.list, foodCalorieList)
                            var listView= this.findViewById<ListView>(R.id.foodList)
                            listView.setAdapter(adapter)
                        }
                    }

                }}
                else{

                }
                uwithp.toSortedMap()
//                for((k,v) in uwithp.toSortedMap()){
//                    Log.e("k:",k.toString())
//                    Log.e("v:",v)
//                }
            }
        }

        }

//    override fun onStart() {
//        super.onStart()
//        var count=1
//
//
//        Handler().postDelayed({
//            /* Create an Intent that will start the Menu-Activity. */
//            for((k,v) in uwithp.toSortedMap()){
//
//                Log.e("Error", "Layout working ?" )
//                foodCalorieList.add("No."+count +"  "+v)
//                count++
//                val adapter = ArrayAdapter(this, R.layout.list, foodCalorieList)
//                var listView= this.findViewById<ListView>(R.id.foodList)
//                listView.setAdapter(adapter)
//            }
//        }, 3000)
////        update.setOnClickListener(){
////            for((k,v) in uwithp.toSortedMap()){
////
////                Log.e("Error", "Layout working ?" )
////                foodCalorieList.add("No."+count +"  "+v)
////                count++
////                val adapter = ArrayAdapter(this, R.layout.list, foodCalorieList)
////                var listView= this.findViewById<ListView>(R.id.foodList)
////                listView.setAdapter(adapter)
////            }
////        }
//
//
//    }


//    fun sort(array1:ArrayList<String>,array2:ArrayList<Double>){
//
//        var tempuser = ArrayList<String>()
//        var tempScore= ArrayList<Double>()
//        while(array1.size!=0) {
//            var bigScore = 0.0;
//            var biguser = "";
//
//            for (i in 0..array1.size) {
//                if (array2.get(i) >= 0) {
//                    bigScore = array2.get(i)
//                    biguser = array1.get(i)
//                }
//            }
//        }
//
//
//    }
    //https://www.programiz.com/kotlin-programming/examples/sort-map-values
//fun sortByValue(hm: HashMap<String, Double>): HashMap<String, Double> {
//    // Create a list from elements of HashMap
//    var sorted = hm.
//
//
//    return sorted;
//}






}