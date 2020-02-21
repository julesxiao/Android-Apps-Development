package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.AccountActivity
import com.example.myapplication.App
import com.example.myapplication.CreateDetailActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.CustomAdapter
import com.example.myapplication.adapter.CustomAdapterDoing
import com.example.myapplication.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_doing.*
import kotlinx.android.synthetic.main.fragment_todo.*
import java.util.ArrayList
import java.util.HashMap

@SuppressLint("ValidFragment")
class DoingFragment (val mContext: Context): Fragment() {
    var tasks = ArrayList<Task>()
    var adapter = CustomAdapterDoing(tasks,mContext,1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_doing, container, false)
    }
    override fun onStart() {
        super.onStart()

        favorites_list_DOING.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        update()

    }

    fun update(){
        val userId = App.firebaseAuth?.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null && documentSnapshot != null) {
                tasks = ArrayList()
                val userData = documentSnapshot.data

                var data = userData?.get("doing") as? ArrayList<HashMap<String, Any>>

                if (data == null) {
                    //TODO: new user

                } else {
                    for (i in data) {
                        var newTask =
                            Task(i.get("date").toString(), i.get("title").toString(), i.get("body").toString(),i.get("photo").toString())
                        tasks.add(newTask)
                    }
                }

                activity?.runOnUiThread {
                    adapter = CustomAdapterDoing(tasks,mContext,0)
                    favorites_list_DOING.adapter = adapter
                }



            } else {
                Toast.makeText(mContext, "Unable to get score", Toast.LENGTH_SHORT).show()
            }
        }


    }

}