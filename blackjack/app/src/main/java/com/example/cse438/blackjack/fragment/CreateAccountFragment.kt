package com.example.cse438.blackjack.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cse438.blackjack.R
import kotlinx.android.synthetic.main.fragment_create_account.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.example.cse438.blackjack.R.*
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.ContentValues.TAG
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.cse438.blackjack.App
import com.example.cse438.blackjack.MusicService
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("ValidFragment")
class CreateAccountFragment(context: Context): Fragment() {

    private var parentContext = context
    private lateinit var auth:FirebaseAuth

    val db = FirebaseFirestore.getInstance()
    // Initialize Firebase Auth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(layout.fragment_create_account, container, false)
    }

    override fun onStart() {
        super.onStart()


        auth = FirebaseAuth.getInstance()
        create_account.setOnClickListener {
            val firstName = first_name.text.toString()
            val lastName = last_name.text.toString()
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()

            if (firstName != "" && lastName != "" && email != "" && username != "" && password != "") {
                App.firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {it2 ->
                    if (it2.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        val userData = HashMap<String, Any>()
                        userData["first_name"] = firstName
                        userData["last_name"] = lastName
                        userData["email"] = email
                        userData["username"] = username

                        db.document("users/${App.firebaseAuth?.currentUser?.uid}")
                            .set(userData)
                            .addOnSuccessListener {
                                activity?.finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(parentContext, "Failed to write user data", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            else {
                Toast.makeText(parentContext, "Must fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
