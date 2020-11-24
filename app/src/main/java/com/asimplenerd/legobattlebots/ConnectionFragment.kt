package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_connection.*
import java.util.*
import java.util.logging.Logger

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
        val botListView = this.view!!.findViewById<RecyclerView>(R.id.availableBattleBotsList)

        goBackBtn.setOnClickListener(this)


        connectBtn.visibility = View.INVISIBLE
        val botListAdapter = BattleBotAdapter(MainActivity.getBots())

        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {

        //AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_animator);
       // AnimationUtils.loadAnimation(MainActivity.,R.anim.button_animator)

        when(v?.id){
            R.id.connectBtn -> showWeaponsFrag()
            R.id.goBackBtn -> showMenuFrag()

            //We really need to generate buttons based on the available bots FOUND, not just two devices.

        }
    }


    fun updateUserName(name : String)
    {
        loggedInText3.text = "Logged in as: $name"
    }

    fun selectBot(id: String, dev: BluetoothDevice?){
        botId = id
        connectBtn.setOnClickListener(this)
        botSelectedText.text = "Bot Selected: $botId"
        Log.i("DEV", "${dev?.address}")
        connectBtn.visibility = View.VISIBLE
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
