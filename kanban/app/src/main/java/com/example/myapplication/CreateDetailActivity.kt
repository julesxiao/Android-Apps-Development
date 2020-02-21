package com.example.myapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_detail.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateDetailActivity : AppCompatActivity() {
    var dates=""
    var contents=""
    var titles=""

    private var currentPhotoPath: String = ""
    private var REQUEST_IMAGE_CAPTURE: Int = 1
    private var REQUEST_LOAD_IMAGE: Int = 2
    private lateinit var photoURI:Uri
    private var date=""
    private lateinit var locationlist : ArrayList<ArrayList<Double>>
    private lateinit var informationlist: ArrayList<ArrayList<String>>


    private var storage = FirebaseStorage.getInstance()
    // Create a storage reference from our app
    // Create a storage reference from our app
    private var storageRef = storage.reference
    private lateinit var mProgress :ProgressDialog
    private var edit=true
    private var section=""
    private var index=-1
    private lateinit var taskExit:Task


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_detail)
        val intent = getIntent()
        edit=intent.getBooleanExtra("edit",true)
       if(edit) {
           index = intent.getStringExtra("index").toInt()
           section = intent.getStringExtra("section")
       }

        mProgress = ProgressDialog(this);

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)+1
        val day = c.get(Calendar.DAY_OF_MONTH)

        var textview2 = findViewById<TextView>(R.id.due_date)
        var textview3 = findViewById<TextView>(R.id.content)
        var textview = findViewById<TextView>(R.id.title)

        if(edit){
            val userId = App.firebaseAuth?.currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId!!).get().addOnCompleteListener { it2 ->
                if (it2.isSuccessful) {
                    val userData = it2.result!!

                    var data = userData.get(section) as java.util.ArrayList<HashMap<String, Any>>


                        for (i in data) {
                            var newTask =
                                Task(i.get("date").toString(), i.get("title").toString(), i.get("body").toString(),i.get("photo").toString())
                            if(data.indexOf(i)==index) {
                                taskExit=newTask
                            }
                        }

                    runOnUiThread {
                        textview2.setText(taskExit.getDate())
                        textview3.setText(taskExit.getBody())
                        textview.setText(taskExit.getTitle())
                        Glide.with(this)
                            .load(taskExit.getPhoto())
                            .into(imageView)


                    }







                } else {
                }


            }


        }
        location.setOnClickListener() {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                var monthchange=mmonth+1
                textview2.setText("" + monthchange + "/" + mdayOfMonth + "/" + myear)
            }, year, month, day)
            dpd.show();

        }


        button5.setOnClickListener() {


            contents = textview3.text.toString();
            titles = textview.text.toString();
            dates = textview2.text.toString();

            //Sanitizing user's input
            if (titles.length >48) {
                Toast.makeText(applicationContext, "Title should be within 48 characters. Keep it short", Toast.LENGTH_SHORT).show()
            }

            else if (titles.length ==0) {
                Toast.makeText(applicationContext, "Please enter the title", Toast.LENGTH_SHORT).show()
            }

            else if (contents.length>120) {
                Toast.makeText(applicationContext, "Details should be within 120 characters", Toast.LENGTH_SHORT).show()
            }
            else if (dates.length>20) {
                Toast.makeText(applicationContext, "Invalid date", Toast.LENGTH_SHORT).show()
            }



            else {
                val db = FirebaseFirestore.getInstance()
                val userId = App.firebaseAuth?.currentUser!!.uid

                db.collection("users").document(userId).get().addOnCompleteListener { it ->
                    if (it.isSuccessful) {

                        val userData = it.result!!
                        //todo
                        var data = ArrayList<Task>();

                        var datahash = userData?.get("todo") as? ArrayList<HashMap<String, Any>>

                        if (datahash == null) {
                            //TODO: new user


                        } else {
                            for (i in datahash) {
                                var newTask =
                                    Task(
                                        i.get("date").toString(),
                                        i.get("title").toString(),
                                        i.get("body").toString(),
                                        i.get("photo").toString()
                                    )
                                data.add(newTask)

                            }
                        }
                        //doing
                        var data2 = ArrayList<Task>();

                        var data2hash = userData?.get("doing") as? ArrayList<HashMap<String, Any>>

                        if (data2hash == null) {
                            //TODO: new user

                        } else {
                            for (i in data2hash) {
                                var newTask =
                                    Task(
                                        i.get("date").toString(),
                                        i.get("title").toString(),
                                        i.get("body").toString(),
                                        i.get("photo").toString()
                                    )
                                data2.add(newTask)
                            }
                        }
                        //done
                        var data3 = ArrayList<Task>();

                        var data3hash = userData?.get("done") as? ArrayList<HashMap<String, Any>>

                        if (data3hash == null) {
                            //TODO: new user

                        } else {
                            for (i in data3hash) {
                                var newTask =
                                    Task(
                                        i.get("date").toString(),
                                        i.get("title").toString(),
                                        i.get("body").toString(),
                                        i.get("photo").toString()
                                    )
                                data3.add(newTask)
                            }
                        }



                        if (data == null) {
                            data = ArrayList<Task>()
                        }
                        if (data2 == null) {
                            data = ArrayList<Task>()
                        }
                        if (data3 == null) {
                            data = ArrayList<Task>()
                        }

                        var task = if (::photoURI.isInitialized) {
                            Task(dates, titles, contents, photoURI.toString())
                        } else {
                            Task(dates, titles, contents, "")
                        }

                        if (edit) {
                            if (section == "todo") {

                                var retask = data.get(index)

                                data.remove(retask)
                                data.add(task)

                            }
                            if (section == "doing") {

                                data2!!.remove(data2.get(index))
                                data2.add(task)
                            }
                            if (section == "done") {
                                data3!!.remove(data3.get(index))
                                data3.add(task)
                            }
                        } else {
                            data.add(task)
                        }


                        val map = hashMapOf(
                            Pair("username", userData.get("username")),
                            Pair("userId", userId),
                            Pair("todo", data),
                            Pair("doing", data2),
                            Pair("done", data3)
                        )
                        db.collection("users").document(userId).set(map)
                        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Unable to get", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
        Camera.setOnClickListener(){
            // what action we want to do
            // is the image capture
            // also: chain statements together we will run it after the first complete
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // go find the in your  phone what you can do in your activity
                //package manager is a list of activities that exist on the phone
                takePictureIntent.resolveActivity(packageManager).also {
                    //try to create a file
                    // whether we have the permission to create a file
                    // we use try and catch
                    //? we can made the file be null
                    val photoFile : File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("ERROR:", "Could not get photo file")
                        null
                    }
                    // if photo file exist
                    // not null also
                    // start the activit, make file provider if we can access the file
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            this,
                            "com.example.myapplication.fileprovider",
                            it
                        )
                        //what is the output we want to store
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }

            }
        }

    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val date = timeStamp
        // find the dir that have pic
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //createTempfile ( file name , extension, where to store)
        return File.createTempFile(
            "JPEG_$timeStamp",
            ".jpg",
            storageDir
        ).apply {
            // remember the path
            // apply we don't need the it.
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic(data)
        }
    }

    private fun galleryAddPic(data:Intent?) {
        mProgress.setMessage("Uploading file.....")
        mProgress.show()
        App.firebaseAuth = FirebaseAuth.getInstance()
        var uid=App.firebaseAuth!!.currentUser!!.uid

// Create a reference to "mountains.jpg"
        var ref = storageRef.child("/"+uid+"/"+currentPhotoPath)

        ref.putFile(photoURI)
            .addOnSuccessListener {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                loadPhoto(this)

            }
            .addOnFailureListener {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }



    }
    fun loadPhoto(context: Context){
        Glide.with(this)
            .load(photoURI)
            .into(imageView)

    }






}