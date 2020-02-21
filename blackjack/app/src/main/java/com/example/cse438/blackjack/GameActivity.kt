package com.example.cse438.blackjack

import android.animation.AnimatorSet
import android.animation.ObjectAnimator

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.cse438.blackjack.util.CardRandomizer
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.widget.LinearLayout
import android.widget.Toast
import com.example.cse438.blackjack.model.Score
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.support.v7.app.AlertDialog;


class GameActivity: AppCompatActivity() {
    private lateinit var mDetector: GestureDetectorCompat
    private var height: Int = 0
    private var width: Int = 0
    private lateinit var move_card: View
    private var count_card: Int = 0
    private var dealerPoints = ArrayList<Int>()
    private var playerPoints = ArrayList<Int>()
    private lateinit var toast: Toast
    private var money:Int = 0

    private var wincount: Long = 0
    private var losecount: Long = 0
    private var dollar=5000;
    private var endofgame=false;

    private var gestureworks: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val metrics = this.resources.displayMetrics
        this.height = metrics.heightPixels
        this.width = metrics.widthPixels
        text_bet.text = "$" + money.toString()
        mDetector = GestureDetectorCompat(this, MyGestureListener())

        //show player's win/lose counts
        //xjq begin
        if(App.firebaseAuth?.currentUser == null){
            Log.e("Error","currentUser is null" )
        }
        else {
            val userId = App.firebaseAuth?.currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId!!).get().addOnCompleteListener { it2 ->
                Log.e("Exception", it2.exception.toString())
                if (it2.isSuccessful) {
                    val userData = it2.result!!

                    var data2score = userData.get("scores") as? HashMap<String, Any>
                    var data2money=userData.get("money") as? Long
                    if(data2money!=null){
                        dollar = data2money.toInt()
                    }
                    Log.e("data2getwin",data2score?.get("win").toString())
                    //Log.e("data2getlose",data2score?.get("lose").toString())
                    if(data2score!=null){
                    wincount = data2score?.get("win") as Long
                    losecount = data2score?.get("lose") as Long}
                    else{

                    }


                    score_text.text = "Win:" + wincount.toString() + "/ Lose:" + losecount.toString()+"\n"+"money:"+dollar
                }else{
                    Toast.makeText(this,"Unable to get score",Toast.LENGTH_SHORT).show()
                }
            }

        }




        //xjq end

        button_decrement_bet.setOnClickListener() {
            if (money > 0) {
                money = money - 100

                text_bet.text = "$" + money.toString()
            }
        }
        button_increment_bet.setOnClickListener() {
            if(money<dollar){
            money = money + 100
            }else{
                Toast.makeText(baseContext, "You are out off money", Toast.LENGTH_SHORT).show()
            }

            text_bet.text = "$" + money.toString()
        }
        button_bet.setOnClickListener() {
            button_decrement_bet.setClickable(false)
            button_increment_bet.setClickable(false)
            button_bet.setClickable(false)
            gestureworks=true
        }

        Timer("Settingup", false).schedule(1000) {
            var array1: ArrayList<Int> = getCard()
            image_dealer_first_card.setImageResource(array1.get(0))
            dealerPoints.add(array1.get(1))

            var array2: ArrayList<Int> = getCard()
            image_player_first_card.setImageResource(array2.get(0))
            playerPoints.add(array2.get(1))
        }

        Timer("Settingup", false).schedule(1200) {
            var array3: ArrayList<Int> = getCard()
            image_player_second_card.setImageResource(array3.get(0))
            playerPoints.add(array3.get(1))
        }

        complete.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            val intent2 = Intent(this, MusicService::class.java)
            stopService(intent2)
            startActivity(intent)

        }
    }

    //https://stackoverflow.com/questions/837951/is-there-an-elegant-way-to-deal-with-the-ace-in-blackjack
    private fun countPoint(array: ArrayList<Int>): Int {
        var sum = 0
        var ace = 0
        var subtract = 0

        for (i in array) {
            if (getValue(i) == 11) {
                ace++
            }
            sum = sum + getValue(i)
            if (sum > 21) {
                if (ace >= 1) {
                    if (ace > subtract) {
                        sum = sum - 10
                        subtract++
                    }

                }
            }
        }
        return sum
    }

    private fun dealerTurn() {
        endofgame=true
        var array5 = getCard()
        image_dealer_second_card.setImageResource(array5.get(0))
        dealerPoints.add(array5.get(1))
        if (countPoint(playerPoints) > 21) {
            gestureworks == false
            updateScore(this, -1, money)
            //Toast.makeText(baseContext, "Player busted! New Round", Toast.LENGTH_SHORT).show()

            var snackbar = Snackbar.make(root_layout,"Player busted! New Round", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Close", View.OnClickListener {
                snackbar.dismiss()
                val intent = Intent(this, GameActivity::class.java)
                finish()
                startActivity(intent)
            })
            snackbar.show()

        } else {
            while (countPoint(dealerPoints) <= 17) {
                count_card = count_card + 1
                when (count_card) {
                    1 -> move_card = a1
                    2 -> move_card = a2
                    3 -> move_card = a3
                    4 -> move_card = a4
                    5 -> move_card = a5
                    6 -> move_card = a6
                    7 -> move_card = a7
                    8 -> move_card = a8
                    9 -> move_card = a9
                    10 -> move_card = a10
                }
                //moveTo(this@GameActivity.width / 2f - move_card.width / 2, -this@GameActivity.height / 2f + move_card.height / 2f)
                moveTo(
                    -this@GameActivity.width / 2f + move_card.width / 2,
                    this@GameActivity.height / 2f - move_card.height / 2f
                )
                drawCard(2)
            }
            if (countPoint(dealerPoints) == 21) {
                gestureworks = false
                updateScore(this, -1, money)
                //Toast.makeText(baseContext, "Player lose! New Round", Toast.LENGTH_SHORT).show()
                var snackbar = Snackbar.make(root_layout,"Player lose! New Round", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, GameActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()


            }
            else if (countPoint(dealerPoints) > 21) {
                gestureworks =false

                updateScore(this, 1, money)
                //Toast.makeText(baseContext, "Player win! New Round", Toast.LENGTH_SHORT).show()
                var snackbar = Snackbar.make(root_layout,"Player win! New Round", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, GameActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()


            }
            else if (countPoint(dealerPoints) > countPoint(playerPoints)) {
                gestureworks = false
                updateScore(this, -1, money)
                //Toast.makeText(baseContext, "Player lose! New Round", Toast.LENGTH_SHORT).show()
                var snackbar = Snackbar.make(root_layout,"Player lose! New Round", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, GameActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()

            }
            else if (countPoint(dealerPoints) < countPoint(playerPoints)) {
                gestureworks = false
                updateScore(this, 1, money)
                //Toast.makeText(baseContext, "Player win! New Round", Toast.LENGTH_SHORT).show()
                var snackbar = Snackbar.make(root_layout,"Player win! New Round", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, GameActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()

            }
            else if (countPoint(dealerPoints) == countPoint(playerPoints)) {
                gestureworks = false
                //Toast.makeText(baseContext, "Tie! New Round", Toast.LENGTH_SHORT).show()
                var snackbar = Snackbar.make(root_layout,"Tie! New Round", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Close", View.OnClickListener {
                    snackbar.dismiss()
                    val intent = Intent(this, GameActivity::class.java)
                    finish()
                    startActivity(intent)
                })
                snackbar.show()

            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if(gestureworks) {
            mDetector.onTouchEvent(event)
            // if we don't have event to handle, it will handle by default in system
            return super.onTouchEvent(event)
//        }
//        return super.onTouchEvent(event)
    }

    private fun drawCard(play_or_deal: Int) {
        // play_or_deal =1 is player hand
        var img: ImageView = ImageView(this)
        var array4 = ArrayList<Int>()
        array4 = getCard()
        img.setImageResource(array4.get(0))
        if (play_or_deal == 1) {
            playerPoints.add(array4.get(1))
        } else {
            dealerPoints.add(array4.get(1))
        }
        addView(img, play_or_deal)
    }

    private fun getCard(): ArrayList<Int> {
        var forreturn = ArrayList<Int>()
        val randomizer: CardRandomizer = CardRandomizer()
        var cardList: ArrayList<Int> = randomizer.getIDs(this) as ArrayList<Int>
        val rand: Random = Random()
        val r: Int = rand.nextInt(cardList.size)
        val id: Int = cardList.get(r)
        val name: String = resources.getResourceEntryName(id)
        forreturn.add(id)
        forreturn.add(r)
        return forreturn
    }

    //convert the cards to its actual score
    public fun getValue(num: Int): Int {
        when (num) {
            0 -> return 10
            13 -> return 10
            26 -> return 10
            39 -> return 10
            1 -> return 2
            14 -> return 2
            27 -> return 2
            40 -> return 2
            2 -> return 3
            15 -> return 3
            28 -> return 3
            41 -> return 3
            3 -> return 4
            16 -> return 4
            29 -> return 4
            42 -> return 4
            4 -> return 5
            17 -> return 5
            30 -> return 5
            43 -> return 5
            5 -> return 6
            18 -> return 6
            31 -> return 6
            44 -> return 6
            6 -> return 7
            19 -> return 7
            32 -> return 7
            45 -> return 7
            7 -> return 8
            20 -> return 8
            33 -> return 8
            46 -> return 8
            8 -> return 9
            21 -> return 9
            34 -> return 9
            47 -> return 9
            // ace
            9 -> return 11
            22 -> return 11
            35 -> return 11
            48 -> return 11
            10 -> return 10
            23 -> return 10
            36 -> return 10
            49 -> return 10
            11 -> return 10
            24 -> return 10
            37 -> return 10
            50 -> return 10
            12 -> return 10
            25 -> return 10
            38 -> return 10
            51 -> return 10
        }
        return -1

    }

    fun moveTo(targetX: Float, targetY: Float) {
        // in order to prevent the x y happen seperatly
        if(gestureworks){val animSetXY = AnimatorSet()

        val x = ObjectAnimator.ofFloat(
            //translated
            move_card,
            "translationX",
            move_card.translationX,
            //the positon we want to move
            targetX
        )

        val y = ObjectAnimator.ofFloat(
            move_card,
            "translationY",
            move_card.translationY,
            targetY
        )

        animSetXY.playTogether(x, y)
        //we can see the move
        animSetXY.duration = 500
        // begin the animation
        animSetXY.start()}
    }


    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        private var swipedistance = 150

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            //doubletap is stand
            if(gestureworks) {
                gestureworks = false
                endofgame=true
                dealerTurn()
                return true
            }
            return false
        }

        //e1 start e2 end
        // swipedistance
        //player's turn, right swipe, hit
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if(gestureworks){
                Log.e("error",gestureworks.toString())
                if (e2.x - e1.x > swipedistance) {
                //TODO:hit
                //deal cards corresponding to the time of hits
                count_card = count_card + 1
                when (count_card) {
                    1 -> move_card = a1
                    2 -> move_card = a2
                    3 -> move_card = a3
                    4 -> move_card = a4
                    5 -> move_card = a5
                    6 -> move_card = a6
                    7 -> move_card = a7
                    8 -> move_card = a8
                    9 -> move_card = a9
                    10 -> move_card = a10
                }
                //moveTo(this@GameActivity.width / 2f - move_card.width / 2, -this@GameActivity.height / 2f + move_card.height / 2f)
                moveTo(
                    -this@GameActivity.width / 2f + move_card.width / 2,
                    this@GameActivity.height / 2f - move_card.height / 2f
                )
                drawCard(1)
                var player = countPoint(playerPoints)
                if (player > 21) {
                    gestureworks=false
                    endofgame=true
                    dealerTurn()
                }
                return true
            }
        }else{
                if (e2.x - e1.x > swipedistance&&!endofgame) {
                    val toast=Toast.makeText(applicationContext,"Bet first!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)

                    toast.show()

                }
            }
            return false
        }
    }

    private fun addView(img: ImageView, play_or_deal: Int) {
        if (play_or_deal == 1) {
            layout_player_hand.addView(img)
        } else {
            layout_dealer_hand.addView(img)
        }
        var layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(img.layoutParams.width, img.layoutParams.height)
        layoutParams.setMargins(-200, 0, 0, 0)
        img.layoutParams = layoutParams
    }
    fun updateScore(context:Context,winorlose:Int, bet:Int){
        val userId = App.firebaseAuth?.currentUser?.uid

        if(userId!=null)
        {
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(App.firebaseAuth!!.currentUser!!.uid).get().addOnCompleteListener { it2 ->
                if (it2.isSuccessful) {
                    val userData = it2.result!!
                    var data2score = userData.get("scores") as? HashMap<String, Any>
                    var data2 = Score(
                        0,
                        App.firebaseAuth!!.currentUser!!.uid,
                        userData.get("username") as String,
                        0)
                    if (userData.get("scores")!=null) {
                        data2 = Score(
                            data2score?.get("win") as Long,
                            App.firebaseAuth!!.currentUser!!.uid,
                            userData.get("username") as String,
                            data2score?.get("lose") as Long
                        )
                    }

                    if (winorlose == 1) {
                        data2.addWin()
                    } else {
                        data2.addLose()
                    }

                    //money
                    var datat = userData.get("money") as? Long
                    var data=datat?.toInt()
                    //Log.e("userData2", userData.get("money").toString())
                    //Log.e("Data2", data.toString())
                    if (userData.get("money") == null) {
                        data=5000
                    }
                    if(data!=null) {
                        if (winorlose == 1) {
                            data = data + bet
                        } else {
                            data = data - bet
                        }
                    }
                    if(data!! <= 0){
                        data=5000
                        val toast=Toast.makeText(applicationContext,"You have no money in your account! We add $5000 to you!",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)

                        toast.show()
//                        val builder = AlertDialog.Builder(this@GameActivity)
//                       //TODO: Dialog does not show up
//                        // Set the alert dialog title
//                        builder.setTitle("App background color")
//                        Log.e("hi","i am here")
//                        // Display a message on alert dialog
//                        builder.setMessage("Are you want to set the app background color to RED?")
//
//                        // Set a positive button and its click listener on alert dialog
//                        builder.setPositiveButton("YES"){dialog, which ->
//                            // Do something when user press the positive button
//                            Toast.makeText(applicationContext,"Ok, add your account.",Toast.LENGTH_SHORT).show()
//                            val dialog: AlertDialog = builder.create()
//
//                            // Display the alert dialog on app interface
//                            dialog.show()

                            // Change the app background color
                        //}


                    }


                    val map2 = hashMapOf(
                        Pair("first_name", userData.get("first_name")),
                        Pair("last_name", userData.get("last_name")),
                        Pair("email", userData.get("email")),
                        Pair("username", userData.get("username")),
                        Pair("scores", data2),
                        Pair("money", data)
                    )

                    db.collection("users").document(App.firebaseAuth!!.currentUser!!.uid).set(map2)
                }
            }
        }
    }
}

