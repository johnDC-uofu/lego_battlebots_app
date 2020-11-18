package com.asimplenerd.legobattlebots

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.play_screen.*
import java.lang.Math.abs
import java.lang.Math.atan
import java.security.Permission
import java.util.*
import kotlin.math.atan

class MainActivity : Joystick.JoystickListener, AppCompatActivity() {

    companion object {
        var username =""
        private const val TAG = "KotlinActivity"
        var speed = "0"
        var direction = "0"
    }

    val scanReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothDevice.ACTION_FOUND -> {
                    val name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE).address
                    if(!deviceAddrs.contains(device)) {
                        Log.d("Scan", "Found bluetooth device: $name:$device")
                        deviceNames.add(name)
                        deviceAddrs.add(device)
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

        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PermissionChecker.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 2)
        }
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(scanReceiver, filter)
        this.supportActionBar!!.hide()
        supportFragmentManager.beginTransaction().add(R.id.interactionFragment, WelcomeFragment()).commitNow()

        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }


    override fun onJoystickMoved(
        xPercent: Float,
        yPercent: Float,
        id: Int
    ) {
        Log.d("Joystick", "X percent: $xPercent Y percent: $yPercent")
        xDirectionVal.text = xPercent.toString()
        yDirectionVal.text = yPercent.toString()

        if(xPercent < 0.8 && xPercent > -0.8 && yPercent < 0.8 && yPercent > -0.8){
            if(xPercent < 0.3 && xPercent > -0.3 && yPercent < 0.3 && yPercent > -0.3) {
                speedValue.text = "Stopped"
                speed = "0"
            }
            else {
                speedValue.text = "Slow"
                speed = "1"
            }

        }
        else {
            speedValue.text = "Fast"
            speed = "2"
        }

        if(xPercent < 0.3 || xPercent > -0.3 || yPercent < 0.3 || yPercent > -0.3)
        {
            if(xPercent > 0) //X is positive
            {
                    if(xPercent > 0.3 && xPercent < 0.6)
                    {
                        dirValue.text = "30"
                        direction = "30"

                    }
                    else if(xPercent > 0.6 && xPercent < 1)
                    {
                        dirValue.text = "60"
                        direction = "60"

                    }
                    else if(xPercent > 1)
                    {
                        dirValue.text = "90"
                        direction = "90"

                    }
                    else
                    {
                        dirValue.text = "0"
                        direction = "0"
                    }
            }
            else  //X is negative
            {
                if(xPercent < -0.3 && xPercent > -0.6)
                {
                    dirValue.text = "330"
                    direction = "330"

                }
                else if(xPercent < -0.6 && xPercent > -1)
                {
                    dirValue.text = "300"
                    direction = "300"

                }
                else if(xPercent < -1)
                {
                    dirValue.text = "270"
                    direction = "270"

                }
                else
                {
                    dirValue.text = "0"
                    direction = "0"
                }

            }
//            val absX = abs(xPercent)
//            val absY = abs(yPercent)
//            val fraction = absY/absX
//            val angle = atan(fraction) * 180/3.14;
//            Log.d("Angle", "$angle")
            //botModel.rotation = angle;

            //dirValue.text = "$angle"
        }
        else {

            dirValue.text = "Stopped"
            speed = "0";
        }

    }
}
