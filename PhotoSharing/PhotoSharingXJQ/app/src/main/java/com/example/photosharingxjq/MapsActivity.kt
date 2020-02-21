package com.example.photosharingxjq

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.app.ActivityCompat
import android.view.Gravity
import android.widget.*
import com.example.photosharingxjq.model.Picinfo
import com.google.android.gms.common.Feature
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MapsActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    var currentPhotoPath :String = " "
    private lateinit var uid: String

    private lateinit var photoURI: Uri
    internal var storage:FirebaseStorage? = null
    internal var storageReference:StorageReference? = null
    var timeStamp: String = ""
    private lateinit var mMap: GoogleMap
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    private var REQUEST_CODE = 1001
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback
    private lateinit var lastLocation: Location
    //private var locatoinlist = ArrayList<LatLng>()

    private var locationUpdateState = false

    private var markersToData = HashMap<LatLng, HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

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
            }

            if (App.firebaseAuth != null && App.firebaseAuth?.currentUser == null) {
                //Log.e("main","2")
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
            // user add their own pictures
//            if(FirebaseAuth.getInstance().currentUser!!.uid != null){

//            }
            //map begin
            map.onCreate(savedInstanceState)
            map.onResume()

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            map.getMapAsync {
                mMap = it
                setUpMap()
                showMarker()
            }

            createLocationRequest()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    //mMap.clear()
                    lastLocation = p0.lastLocation
                    //placeMarkerOnMap(lastLocation)
                }
            }

            //show marker end

            takepicbuttion.setOnClickListener(){
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager).also {
                        val photoFile : File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            Log.d("ERROR:", "Could not get photo file")
                            null
                        }

                        photoFile?.also {
                            photoURI = FileProvider.getUriForFile(
                                this,
                                "com.example.photosharingxjq.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            }

            signoutbutton.setOnClickListener (){
                App.firebaseAuth?.signOut()
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }

        }
        ft.commit()
    }
    override fun onStart(){
        super.onStart()

        showMarker()
    }

    private fun createImageFile(): File {
        timeStamp= SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_$timeStamp",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    //once user clicks confirm, it will upload the pictures taken
    //reference: https://www.youtube.com/watch?v=osEtVO-JJjE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if ((requestCode == REQUEST_IMAGE_CAPTURE ) && resultCode == Activity.RESULT_OK) {
            //update pictures into the cloud
            if (currentPhotoPath != null) {
                //displaying a progress dialog while upload is going on
                val progressDialog = ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                uid = FirebaseAuth.getInstance().currentUser!!.uid
                val imageRef = storageReference!!.child("/images/" +  uid + "/"+timeStamp +"/" + currentPhotoPath);

                imageRef.putFile(photoURI)
                    .addOnSuccessListener {
                        progressDialog.dismiss();
                        val toast= Toast.makeText(getApplicationContext(), "File Uploaded Success ", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss();
                        val toast = Toast.makeText(getApplicationContext(), "File Uploaded Failed", Toast.LENGTH_LONG).show();
                    }
                    .addOnProgressListener{ taskSnapshot ->
                        var progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt()+ "%...")
                    }
            }
            //if there is not any file
            else {
                //you can display an error toast
                Toast.makeText(getApplicationContext(), "No file existed", Toast.LENGTH_LONG).show();
            }

            //save pic info into database
            val userId = App.firebaseAuth?.currentUser?.uid

            if(userId!=null)
            {
                val db = FirebaseFirestore.getInstance()
                var initcount:Int = 0
                db.collection("users").document(App.firebaseAuth!!.currentUser!!.uid).get().addOnCompleteListener { it2 ->
                    if (it2.isSuccessful) {
                        val userData = it2.result!!
                        if(userData != null){
                            //for pictures info
                            var data2picture = userData.get("pictures") as? ArrayList<Picinfo>

                            if(data2picture == null){
                                data2picture = ArrayList()
                            }

                            data2picture.add(Picinfo(
                                userId,
                                timeStamp,
                                photoURI.toString(),
                                LatLng(lastLocation.latitude, lastLocation.longitude),
                                initcount
                            ))

//                            //for location list
//                            var data3piclist = userData.get("location_list") as? ArrayList<LatLng>
//                            if(data3piclist == null){
//                                data3piclist = ArrayList()
//                            }
//                            data3piclist.add(LatLng(lastLocation.latitude, lastLocation.longitude))
                            val map2 = hashMapOf(
                                Pair("first_name", userData.get("first_name")),
                                Pair("last_name", userData.get("last_name")),
                                Pair("email", userData.get("email")),
                                Pair("username", userData.get("username")),
                                Pair("pictures", data2picture),
                                Pair("user_id", userId)
                            )
                            db.collection("users").document(App.firebaseAuth!!.currentUser!!.uid).set(map2)
//                            runOnUiThread{
//                                findViewById<Button>(R.id.signoutbutton).text = "SIGN OUT"
                            showMarker()
//                            }
                    }
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if(!locationUpdateState) {
            startLocationUpdates()
        }
        //to make sure code doesn't run in background, then you could notice the position change
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationUpdateState = false
        this.fusedLocationClient.removeLocationUpdates(locationCallback)
        //to make sure code doesn't run in background
        map.onPause()
    }


    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }

    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 10000

        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        var builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
    }

    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        this.fusedLocationClient.lastLocation.addOnSuccessListener {
            lastLocation = it
            //placeMarkerOnMap(lastLocation)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lastLocation.latitude,lastLocation.longitude)))
        }
    }

    private fun placeMarkerOnMap(lastLocation: Location){
        var mark: MarkerOptions = MarkerOptions()
        mark.position(LatLng(lastLocation.latitude, lastLocation.longitude))
        mark.title("I am HERE!")
        mMap.addMarker(mark)
    }
    //EXTRACT location_list FROM DATABASE and show it on the map
    private fun showMarker(){
        val userId = App.firebaseAuth?.currentUser?.uid
        var countAll:Int = 0
        var countEach:Int = 0
        if(userId != null){
            val db = FirebaseFirestore.getInstance()
            db.collection("users").get().addOnCompleteListener{intent->
                if(intent.isSuccessful) {
                    val data = intent.result!!
                    //Log.e("XJQTEST", data.toString())
                    for (eachUser in data) {
                        var username: String = eachUser.get("username") as String
                        //store each picture's location
                        var locatoinlist = ArrayList<LatLng>()
                        //store each picture's date
                        var datelist = ArrayList<String>()
                        //store each picture's path
                        var pathlist = ArrayList<String>()
                        //store each picture's count
                        var countlist = ArrayList<String>()
                        var currentId:String = eachUser.get("user_id") as String
                        val picData = eachUser.get("pictures") as? ArrayList<HashMap<String, Any>>
                        countAll = data.indexOf(eachUser)
                        if (picData != null && picData.size > 0) {
                            for (eachPic in picData) {
                                countEach = picData.indexOf(eachPic)
                                if (eachPic != null) {
                                    var locationPic = eachPic.get("photoLoc") as HashMap<String, Any>
                                    var latitudePic = locationPic.get("latitude") as Double
                                    var longitutePic = locationPic.get("longitude") as Double
                                    var loclist: LatLng = LatLng(latitudePic.toDouble(), longitutePic.toDouble())
                                    var datePic = eachPic.get("photoDate") as String
                                    var pathPic = eachPic.get("photoPath") as String
                                    var countlike = eachPic.get("count") as Long
                                    locatoinlist.add(loclist)
                                    datelist.add(datePic)
                                    pathlist.add(pathPic)
                                    countlist.add(countlike.toString())
                                }
                            }
                            // make sure to excute setMarker after locationlist finished adding
                            runOnUiThread(){
                                if (countEach == picData.size - 1) {
                                    //differnt users have different colors
                                    var color: Float = ((countAll * 100 + 1) % 360).toFloat()
                                    //setMarker(color)
                                    for(loc in locatoinlist){
                                        var mark: MarkerOptions = MarkerOptions()
                                        var index: Int = locatoinlist.indexOf(loc)
                                        var tempDate: String = datelist[index]
                                        var tempPath: String = pathlist[index]
                                        var tempCount: String = countlist[index]
                                        mark.position(loc)
                                        //val hue:Float = 220f  //(Range: 0 to 360)
                                        mark.icon(BitmapDescriptorFactory.defaultMarker(color))

                                        this.markersToData[loc] = hashMapOf(
                                            Pair("Username", username),
                                            Pair("Date", tempDate),
                                            Pair("Path", tempPath),
                                            Pair("Count", tempCount),
                                            Pair("Id", currentId)
                                        )
                                        mMap.addMarker(mark)
                                        mMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
                                            //OPEN A NEW ACTIVITY to show details of each picture
                                            val intent = Intent(this, DetailActivity::class.java)

                                            val data: HashMap<String, String>? = this.markersToData[marker.position];
                                            intent.putExtra("USER_LOCATOIN", marker.position.toString())
                                            intent.putExtra("USER_NAME",data!!["Username"])
                                            intent.putExtra("USER_DATE",data["Date"])
                                            intent.putExtra("USER_PATH",data["Path"])
                                            intent.putExtra("USER_COUNT",data["Count"])
                                            intent.putExtra("USER_ID", data["Id"])

                                            startActivity(intent)
                                            return@OnMarkerClickListener false
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

