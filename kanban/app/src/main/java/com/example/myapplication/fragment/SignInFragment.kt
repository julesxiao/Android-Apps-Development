package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.App
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

@SuppressLint("ValidFragment")
class SignInFragment(context: Context): Fragment() {

    private var parentContext = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onStart() {
        super.onStart()


        sign_in.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            App.firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { it2 ->
                if (it2.isSuccessful) {

                    activity?.finish()
                }
                else {
                    Toast.makeText(parentContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}