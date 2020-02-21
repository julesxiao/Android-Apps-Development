package com.example.myapplication.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.SyncStateContract.Helpers.update
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.App
import com.example.myapplication.CreateDetailActivity
import com.example.myapplication.DetailActivity
import com.example.myapplication.R
import com.example.myapplication.fragment.DoingFragment
import com.example.myapplication.fragment.TodoFragment
import com.example.myapplication.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_todo.view.*
import java.util.HashMap

class CustomAdapter(val tasks:ArrayList<Task> , val context: Context, val tab:Int): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var v:View
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {


             v = LayoutInflater.from(p0?.context).inflate(R.layout.recycler_view_item, p0, false)


        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val task :Task = tasks[p1]

        p0.textViewName.text = task.getTitle()
        p0.textViewAddress.text = task.getDate()
        p0.right.setOnClickListener(){
            val userId = App.firebaseAuth?.currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            var newTodo = ArrayList<Task>();
            var newDoing = ArrayList<Task>();
            db.collection("users").document(userId!!).get().addOnCompleteListener { it2 ->
                if (it2.isSuccessful) {
                    val userData = it2.result!!

                    var data = userData.get("todo") as? java.util.ArrayList<HashMap<String, Any>>
                    var data2 = userData.get("doing") as? ArrayList<HashMap<String,Any>>
                    if(data2!=null){
                        for (i in data2) {
                            var newDoingTask =
                                Task(i.get("date").toString(), i.get("title").toString(), i.get("body").toString(),i.get("photo").toString())
                            newDoing.add(newDoingTask)
                        }
                    }
                    if (data == null) {
                        //TODO: new user

                    } else {
                        for (i in data) {
                            var newTask =
                                Task(i.get("date").toString(), i.get("title").toString(), i.get("body").toString(),i.get("photo").toString())
                            if(data.indexOf(i)!=p1) {
                                newTodo.add(newTask)
                            }else{
                                newDoing.add(newTask)
                            }
                        }
                    }

                    val map = hashMapOf(
                        Pair("username", userData.get("username")),
                        Pair("userId", userId),
                        Pair("todo", newTodo),
                        Pair("doing", newDoing),
                        Pair("done", userData.get("done"))
                    )
                    db.collection("users").document(userId).set(map)




                } else {

                }


            }
        }
        p0.row.setOnClickListener {
            val intent = Intent(context, CreateDetailActivity::class.java)
            //intent.putExtra("PRODUCT", product)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("index", p1.toString())
            intent.putExtra("section","todo")
            intent.putExtra("edit",true)
            context.startActivity(intent)
        }

        p0.row.setOnLongClickListener {
            val dialogBuilder = AlertDialog.Builder(context)

            // set message of alert dialog
            dialogBuilder.setMessage("Do you want to delete this task?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> finish(p1)
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete!")
            // show alert dialog
            alert.show()

            true;
        }
    }
    private fun finish(index:Int) {
        val userId = App.firebaseAuth?.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        var newTodo = ArrayList<Task>();
        var newDoing = ArrayList<Task>();
        db.collection("users").document(userId!!).get().addOnCompleteListener { it2 ->
            if (it2.isSuccessful) {
                val userData = it2.result!!

                var data = userData.get("todo") as java.util.ArrayList<HashMap<String, Any>>



                for (i in data) {
                    var newTask =
                        Task(
                            i.get("date").toString(),
                            i.get("title").toString(),
                            i.get("body").toString(),
                            i.get("photo").toString()
                        )
                    if (data.indexOf(i) != index) {
                        newTodo.add(newTask)
                    }


                }


                val map = hashMapOf(
                    Pair("username", userData.get("username")),
                    Pair("userId", userId),
                    Pair("todo", newTodo),
                    Pair("doing", userData.get("doing")),
                    Pair("done", userData.get("done"))
                )
                db.collection("users").document(userId).set(map)





            } else {

            }
        }
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val row = itemView
        val textViewName = itemView.findViewById<TextView>(R.id.textView)
        val textViewAddress = itemView.findViewById<TextView>(R.id.textView2)
        val right = itemView.findViewById<Button>(R.id.rightButton)
    }
}