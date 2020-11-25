package com.asimplenerd.legobattlebots

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_connection.*
import kotlinx.android.synthetic.main.play_screen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.*
import java.util.concurrent.locks.Lock
import kotlin.collections.ArrayList


class MainActivity : Joystick.JoystickListener, AppCompatActivity() {

    companion object {
        var username = ""
        private const val TAG = "KotlinActivity"
        var xPos = "0"
        var yPos = "0"
        var decimalPlaces = 3
        var botList : ArrayList<BattleBot> = ArrayList()
        var deviceAddrs = ArrayList<String>()
        var deviceNames = ArrayList<String>()
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val adapterLock : Mutex = Mutex(false)
        //var mAuth: FirebaseAuth? = null
        fun getBots(): ArrayList<BattleBot> {
            return botList
        }
        fun startScan(){
            if(!adapter.isDiscovering) {
                adapter.startDiscovery()
                Log.i("Discovery", "BLUETOOTH DISC STARTED")
            }
        }
        fun stopScan(){
            if(adapter.isDiscovering) {
                adapter.cancelDiscovery()
                Log.i("Discovery", "BLUETOOTH DISC STOPPED")
            }
        }
    }

    val scanReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothDevice.ACTION_FOUND -> {
                    val name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceAddr = device.address
                    if(!deviceAddrs.contains(deviceAddr) && name != null && name.contains(BattleBot.BOT_IDENTIFIER)) {
                        Log.d("Scan", "Found bluetooth device: $name:$deviceAddr")
                        deviceNames.add(name)
                        deviceAddrs.add(deviceAddr)
                        //try to get bot id now
                        val bot = BattleBot(device, name)
                        if(!botList.contains(bot)) {
                            botList.add(bot)
                            Log.d("Adding Bot", "Bot has ID ${bot.getID()}")
                        }
                    }
                }
                else -> Log.d("Scan", "Got here")
            }
        }

    }

    val deviceNames = ArrayList<String>()
    val deviceAddrs = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val joystick = this.joystick
        botList = ArrayList()
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PermissionChecker.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 2)
        }
        val filter = IntentFilter()
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(scanReceiver, filter)
        //Ensure bluetooth adapter is enabled
        if(!bluetoothAdapter.isEnabled){
            bluetoothAdapter.enable()
        }
        for(dev : BluetoothDevice in bluetoothAdapter.bondedDevices){
            if(dev.name == BattleBot.BOT_IDENTIFIER) {
                val bot = BattleBot(dev, dev.name)
                if(!botList.contains(bot)) {
                    botList.add(bot)
                    Log.d("Added device", "${dev.name} : ${dev.address}")
                }
            }
        }
        bluetoothAdapter.startDiscovery()
        this.supportActionBar!!.hide()
        supportFragmentManager.beginTransaction().add(R.id.interactionFragment, WelcomeFragment()).commitNow()



//        mAuth = FirebaseAuth.getInstance();
//
//        // Write a message to the database
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        myRef.setValue("Hello, World!")

        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue<String>()
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })

    }

    public fun getBots(): ArrayList<BattleBot> {
        return botList
    }

//
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = mAuth!!.currentUser
//    }


    fun Float.roundTo(n : Int) : Float {
        return "%.${n}f".format(this).toFloat()
    }

    override fun onJoystickMoved(
        xPercent: Float,
        yPercent: Float,
        id: Int
    ) {

        Log.d("Joystick", "X percent: $xPercent Y percent: $yPercent")

        xDirectionVal.text = xPercent.roundTo(decimalPlaces).toString()
        yDirectionVal.text = yPercent.roundTo(decimalPlaces).toString()

        xPos = xPercent.roundTo(decimalPlaces).toString()
        yPos = yPercent.roundTo(decimalPlaces).toString()


        if(xPercent < 0.9 && xPercent > -0.9 && yPercent < 0.9 && yPercent > -0.9){
            if(xPercent < 0.1 && xPercent > -0.1 && yPercent < 0.1 && yPercent > -0.1) {
                //speedValue.text = "Stopped"
                speedBar.progress = 0
            }
            else if(xPercent < 0.3 && xPercent > -0.3 && yPercent < 0.3 && yPercent > -0.3) {
                //speedValue.text = "Stopped"
                speedBar.progress = 20
            }
            else if(xPercent < 0.5 && xPercent > -0.5 && yPercent < 0.5 && yPercent > -0.5) {
                //speedValue.text = "Stopped"
                speedBar.progress = 40
            }
            else if(xPercent < 0.7 && xPercent > -0.7 && yPercent < 0.7 && yPercent > -0.7) {
                //speedValue.text = "Stopped"
                speedBar.progress = 60
            } else {
                //speedValue.text = "Slow"
                speedBar.progress = 80
            }

        }
        else {
            //speedValue.text = "Fast"
            speedBar.progress = 100
        }

//        if(xPercent < 0.3 || xPercent > -0.3 || yPercent < 0.3 || yPercent > -0.3)
//        {
//            if(xPercent > 0) //X is positive
//            {
//                    if(xPercent > 0.3 && xPercent < 0.6)
//                    {
//                        dirValue.text = "30"
//
//                    }
//                    else if(xPercent > 0.6 && xPercent < 1)
//                    {
//                        dirValue.text = "60"
//
//                    }
//                    else if(xPercent > 1)
//                    {
//                        dirValue.text = "90"
//
//                    }
//                    else
//                    {
//                        dirValue.text = "0"
//                    }
//            }
//            else  //X is negative
//            {
//                if(xPercent < -0.3 && xPercent > -0.6)
//                {
//                    dirValue.text = "330"
//
//                }
//                else if(xPercent < -0.6 && xPercent > -1)
//                {
//                    dirValue.text = "300"
//
//                }
//                else if(xPercent < -1)
//                {
//                    dirValue.text = "270"
//
//                }
//                else
//                {
//                    dirValue.text = "0"
//                }
//
//            }
////            val absX = abs(xPercent)
////            val absY = abs(yPercent)
////            val fraction = absY/absX
////            val angle = atan(fraction) * 180/3.14;
////            Log.d("Angle", "$angle")
//            //botModel.rotation = angle;
//
//            //dirValue.text = "$angle"
//        }
//        else {
//
//            dirValue.text = "Stopped"
//        }

    }
}
