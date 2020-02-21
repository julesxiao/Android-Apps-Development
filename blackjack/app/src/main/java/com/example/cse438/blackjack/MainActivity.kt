package com.example.cse438.blackjack

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cse438.blackjack.util.CardRandomizer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private var isNetworkConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {

        //start the background music
        val intent = Intent(this, MusicService::class.java)
        startService(intent)

        FirebaseApp.initializeApp(this)

        Log.e("FAVORITE", "Added favorite but came back here...")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // Load Fragment into View
        val fm = supportFragmentManager

        // add
        val ft = fm.beginTransaction()

        if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
        }

        else {
            Log.e("NETWORK", "connected")
            if (App.firebaseAuth == null) {
                App.firebaseAuth = FirebaseAuth.getInstance()
                //Log.e("main2",FirebaseAuth.getInstance().toString())
                //Log.e("main3",App.firebaseAuth?.currentUser.toString())
            }

            if (App.firebaseAuth != null && App.firebaseAuth?.currentUser == null) {
                //Log.e("main","2")
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
            newButton.setOnClickListener (){
                App.firebaseAuth?.signOut()
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }


            playButton.setOnClickListener (){
                //Log.e("playbutton","work")
                val intent = Intent(this,GameActivity::class.java)

                startActivity(intent)
            }
            rankButton.setOnClickListener (){
                //Log.e("playbutton","work")
                val intent = Intent(this,RankActivity::class.java)

                startActivity(intent)
            }
            pauseButton.setOnClickListener(){
                stopService(intent)
            }
            startButton.setOnClickListener(){
                startService(intent)
            }

            val randomizer: CardRandomizer = CardRandomizer()
            var cardList: ArrayList<Int> = randomizer.getIDs(this) as ArrayList<Int>
            val rand: Random = Random()
        }
        ft.commit()
    }
}
