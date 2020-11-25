package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_connection.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

public val BATTLEBOT_UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee")

class ConnectionFragment : Fragment(), View.OnClickListener{

        companion object {
            var botId = "100"
            var battleBot : BattleBot? = null
            fun newInstance() = ConnectionFragment()
            var recycler : RecyclerView? = null
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
        recycler = botListView
        goBackBtn.setOnClickListener(this)

        connectBtn.visibility = View.INVISIBLE
        val botListAdapter = BattleBotAdapter(MainActivity.getBots())
        botListView.adapter = botListAdapter
        botListView.layoutManager = LinearLayoutManager(context)
        botListView.addItemDecoration(DividerItemDecoration(botListView.context, DividerItemDecoration.VERTICAL))
        botListView.invalidate()

        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {

        //AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_animator);
       // AnimationUtils.loadAnimation(MainActivity.,R.anim.button_animator)

        when(v?.id){
            R.id.connectBtn -> {
                    if (battleBot != null && battleBot!!.connect()) {
                        //Stop adapter scan for faster connection
                            MainActivity.stopScan()
                            showWeaponsFrag()
                    }
                    else
                        Toast.makeText(
                            context,
                            "FAILED TO CONNECT TO BOT WITH ID: $botId",
                            Toast.LENGTH_LONG
                        ).show()
            }
            R.id.goBackBtn -> showMenuFrag()

            //We really need to generate buttons based on the available bots FOUND, not just two devices.

        }
    }


    fun updateUserName(name : String)
    {
        loggedInText3.text = "Logged in as: $name"
    }

    fun selectBot(bot : BattleBot){
        botId = bot.getID().toString()
        battleBot = bot
        connectBtn.setOnClickListener(this)
        botSelectedText.text = "Bot Selected: $botId"
        Log.i("DEV", "${bot.getDevice()?.address}")
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
