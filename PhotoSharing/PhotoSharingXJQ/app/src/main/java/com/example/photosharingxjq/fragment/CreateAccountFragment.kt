package com.example.photosharingxjq.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.photosharingxjq.App
import com.example.photosharingxjq.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_create_account.*


@SuppressLint("ValidFragment")
class CreateAccountFragment(context: Context): Fragment() {

    private var parentContext = context
    private lateinit var auth:FirebaseAuth

    val db = FirebaseFirestore.getInstance()
    // Initialize Firebase Auth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_create_account, container, false)
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
            val userid = App.firebaseAuth?.currentUser?.uid

            if (firstName != "" && lastName != "" && email != "" && username != "" && password != "") {
                App.firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {it2 ->
                    if (it2.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        val userData = HashMap<String, Any>()
                        userData["first_name"] = firstName
                        userData["last_name"] = lastName
                        userData["email"] = email
                        userData["username"] = username
                        userData["user_id"] = userid.toString()
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
