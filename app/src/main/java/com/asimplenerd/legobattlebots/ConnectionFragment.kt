package com.asimplenerd.legobattlebots

import android.app.AlertDialog
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_connection.*
import kotlinx.coroutines.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

val BATTLEBOT_UUID: UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee")

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
            return inflater.inflate(R.layout.fragment_connection, container, false)
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
                    v.isEnabled = false
                    val dialog = AlertDialog.Builder(context)
                    val connectionSpinner = layoutInflater.inflate(R.layout.connection_dialog, null)
                    val alert = dialog.setView(connectionSpinner).setCancelable(false).show()
                    val connRoutine = GlobalScope.launch(Dispatchers.Main){
                        MainActivity.stopScan()

                        if (battleBot != null && battleBot!!.connect()) {
                            //Stop adapter scan for faster connection
                            //connWheel.visibility = View.GONE
                        } else {
                            //connWheel.visibility = View.GONE
                            Toast.makeText(this@ConnectionFragment.context, "Unable to connect to Bot", Toast.LENGTH_SHORT).show()
                            this.cancel("Failed to connect to Bot")
                        }
                    }.invokeOnCompletion { throwable: Throwable? ->
                        if(battleBot?.isConnected()!!)
                            showWeaponsFrag()
                        alert.cancel()
                    }
                v.isEnabled = true
            }
            R.id.goBackBtn -> showMenuFrag()

            //We really need to generate buttons based on the available bots FOUND, not just two devices.

        }
    }


    private fun updateUserName(name : String)
    {
        loggedInText3.text = getString(R.string.logged_in_user, name)
    }

    fun selectBot(bot : BattleBot){
        botId = bot.getID().toString()
        battleBot = bot
        connectBtn.setOnClickListener(this)
        botSelectedText.text = getString(R.string.bot_selected, botId)
        Log.i("DEV", "${bot.getDevice()?.address}")
        connectBtn.visibility = View.VISIBLE
    }

    private fun showWeaponsFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WeaponSelectFragment()).addToBackStack("Connection").commit()
    }

    private fun showMenuFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).commit()
    }

}
