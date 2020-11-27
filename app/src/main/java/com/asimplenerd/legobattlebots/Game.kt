package com.asimplenerd.legobattlebots

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.asimplenerd.legobattlebots.WeaponSelectFragment.Companion.attackId
import kotlinx.android.synthetic.main.play_screen.*

class Game : Fragment(), View.OnTouchListener {

    companion object {

        const val MAX_ARMOR_LEVEL = 3
        var attacks = 0
        var armor = MAX_ARMOR_LEVEL

    }


    private val armorLoop = GameLoop(this, 100)
    private val outputLoop = GameLoop(this, 10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        ConnectionFragment.battleBot?.enterCombat()


        return inflater.inflate(R.layout.play_screen, container, false)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        speedBar.progress = 0
        super.onActivityCreated(savedInstanceState)
        val attackBtn1 = this.view!!.findViewById<Button>(R.id.attackBtn1)
        val attackBtn2 = this.view!!.findViewById<Button>(R.id.attackBtn2)
        attackBtn1.setOnTouchListener(this)
        attackBtn2.setOnTouchListener(this)
        attackBtn1.text = attackId

        updateUserName(MainActivity.username)

        updateBotId(ConnectionFragment.botId)
        if(armorLoop.isAlive)
            armorLoop.endLoop()
        armorLoop.startLoop(false)
        if(outputLoop.isAlive)
            outputLoop.endLoop()
        outputLoop.startLoop(true)
    }

    private fun updateUserName(name : String)
    {
        loggedInText2.text = getString(R.string.logged_in_user, name)
    }

    private fun updateBotId(id : String)
    {
        botName.text = getString(R.string.bot_connected, id)
    }


//    fun mainAttack(){
//        attacks += 1
//        ConnectionFragment.battleBot?.sendDataToBot("primary: start")
//    }
//
//    fun sideAttack(){
//        ConnectionFragment.battleBot?.sendDataToBot("secondary: start")
//    }

    private fun gameOver(){
        //Disconnect from the bot
        ConnectionFragment.battleBot?.disconnect()
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, GameOverFragment()).commit()
    }

    fun update(output : Boolean) {

        if(output)
        {
            //Log.d("Updated", "output; ")
            try {
                attacks = 0
            } catch (e: Exception) {
                outputLoop.endLoop()
                e.printStackTrace()
            }
        }
        else {
            //Log.d("Updated", "armor; ")
            try {





                val percent = ConnectionFragment.battleBot!!.getRemainingHealthPercentage() * 100.0
                Log.d("Percent: ", percent.toString())
                hpBar.progress = percent.toInt()

                if (hpBar.progress <= 0) {
                    gameOver()
                    hpBar.progress = 100
                    armor = MAX_ARMOR_LEVEL
                    armorLoop.endLoop()
                    outputLoop.endLoop()
                }
            } catch (e: Exception) {
                armorLoop.endLoop()
                e.printStackTrace()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(v?.id){
            R.id.attackBtn1 -> {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    Log.d("Attack", "pressed")
                    ConnectionFragment.battleBot?.sendDataToBot("primary: pressed")
                }
                else if(event?.action == MotionEvent.ACTION_UP){
                    Log.d("Attack", "released")
                    ConnectionFragment.battleBot?.sendDataToBot("primary: released")
                }
                return true
            }
            R.id.attackBtn2 -> {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    Log.d("Secondary", "pressed")
                    ConnectionFragment.battleBot?.sendDataToBot("secondary: pressed")
                }
                else if(event?.action == MotionEvent.ACTION_UP){
                    Log.d("Secondary", "released")
                    ConnectionFragment.battleBot?.sendDataToBot("secondary: released")
                }
                return true
            }
        }
        v?.performClick()
        return false
    }
}