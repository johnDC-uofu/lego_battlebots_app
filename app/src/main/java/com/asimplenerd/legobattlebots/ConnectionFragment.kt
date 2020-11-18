package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_connection.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

public val BATTLEBOT_UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee")

class ConnectionFragment : Fragment(), View.OnClickListener  {

        companion object {
            var botId = "100"

            fun newInstance() = ConnectionFragment()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_connection, container, false)
            val manager = activity!!.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter
            //adapter.startDiscovery()
            return view
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val connectBtn = this.view!!.findViewById<Button>(R.id.connectBtn)
        val goBackBtn = this.view!!.findViewById<Button>(R.id.goBackBtn)
        val selectBtn1 = this.view!!.findViewById<Button>(R.id.botSelectBtn1)
        val selectBtn2 = this.view!!.findViewById<Button>(R.id.botSelectBtn2)
        goBackBtn.setOnClickListener(this)
        selectBtn1.setOnClickListener(this)
        selectBtn2.setOnClickListener(this)

        connectBtn.visibility = View.INVISIBLE


        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {

        //AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_animator);
       // AnimationUtils.loadAnimation(MainActivity.,R.anim.button_animator)

        when(v?.id){
            R.id.connectBtn -> showWeaponsFrag()
            R.id.goBackBtn -> showMenuFrag()

            R.id.botSelectBtn1 -> selectedBot1()
            R.id.botSelectBtn2 -> selectedBot2()
        }
    }


    fun updateUserName(name : String)
    {
        loggedInText3.text = "Logged in as: " + name
    }

    fun selectedBot1()
    {
        botId = botIdText1.text.toString()
        connectBtn.setOnClickListener(this)


        botSelectedText.text = "Bot Selected: " + botIdText1.text
        connectBtn.visibility = View.VISIBLE
        connectBtn.isEnabled
    }

    fun selectedBot2()
    {
        botId = botIdText2.text.toString()
        connectBtn.setOnClickListener(this)

        botSelectedText.text = "Bot Selected: " + botIdText2.text
        connectBtn.visibility = View.VISIBLE
        connectBtn.isEnabled
    }


    fun showWeaponsFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WeaponSelectFragment()).addToBackStack("Game").commit()
    }

    fun showMenuFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
    }

}
