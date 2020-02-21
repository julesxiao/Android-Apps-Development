package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.*
import com.example.myapplication.adapter.CustomAdapter
import com.example.myapplication.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.util.ArrayList
import java.util.HashMap

@SuppressLint("ValidFragment")

class TodoFragment  (val mContext: Context): Fragment() {
    var tasks = ArrayList<Task>()
    var adapter = CustomAdapter(tasks,mContext,0)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=layoutInflater.inflate(R.layout.fragment_todo, container, false)

        return layoutInflater.inflate(R.layout.fragment_todo, container, false)



    }
    override fun onStart() {
        super.onStart()


        fab2.setOnClickListener { view ->
            val intent = Intent(getActivity(), CreateDetailActivity::class.java)
            intent.putExtra("edit",false)
            getActivity()!!.startActivity(intent)
        }
        newButton.setOnClickListener() {
            App.firebaseAuth?.signOut()
            val intent = Intent(getActivity(), AccountActivity::class.java)
            getActivity()?.startActivity(intent)
        }

        favorites_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //favorites_list.adapter = adapter

        update()
    }
     fun update(){
        val userId = App.firebaseAuth?.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

         db.collection("users").document(userId!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
             if (firebaseFirestoreException == null && documentSnapshot != null) {
                 tasks = ArrayList()
                 val userData = documentSnapshot.data

                 var data = userData?.get("todo") as? ArrayList<HashMap<String, Any>>

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
                     adapter = CustomAdapter(tasks,mContext,0)
                     if(favorites_list!=null){
                     favorites_list.adapter = adapter}
                 }



             } else {
                 Toast.makeText(mContext, "Unable to get score", Toast.LENGTH_SHORT).show()
             }
         }
    }

}