package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    val fm = supportFragmentManager

    // add
    val ft = fm.beginTransaction()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo


        // Load Fragment into View


        if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
        }

        else {
            FirebaseApp.initializeApp(this)
            Log.e("NETWORK", "connected")
            if (App.firebaseAuth == null) {
                App.firebaseAuth = FirebaseAuth.getInstance()
                //Log.e("main2",FirebaseAuth.getInstance().toString())
                //Log.e("main3",App.firebaseAuth?.currentUser.toString())
            }

            if (App.firebaseAuth != null && App.firebaseAuth?.currentUser == null) {
                //Log.e("main","2")
                Log.e("condition",(App.firebaseAuth != null && App.firebaseAuth?.currentUser == null).toString())
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }

        }
//        val ada[ter]


    }
    override fun onStart(){
        super.onStart()
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null) {
            val intent = Intent(this, TabActivity::class.java)
            startActivity(intent)
            ft.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
