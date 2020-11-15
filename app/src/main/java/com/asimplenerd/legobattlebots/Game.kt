package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_connection.*
import kotlinx.android.synthetic.main.play_screen.*

class Game : Fragment(), View.OnClickListener {

    companion object {
        val botId = "100"
    }


    val gameLoop = GameLoop(this, 100)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        Log.d("Right Joystick", "got here: ");


        return inflater.inflate(R.layout.play_screen, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val attackBtn1 = this.view!!.findViewById<Button>(R.id.attackBtn1)
        val attackBtn2 = this.view!!.findViewById<Button>(R.id.attackBtn2)
        attackBtn1.setOnClickListener(this)
        attackBtn2.setOnClickListener(this)

        updateUserName(MainActivity.username)

        updatebotId(ConnectionFragment.botId)
        gameLoop.startLoop()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.attackBtn1 -> mainAttack()
            R.id.attackBtn2 -> sideAttack()
        }
    }

    fun updateUserName(name : String)
    {
        loggedInText2.text = "Logged in as: " + name
    }

    fun updatebotId(id : String)
    {
        botName.text = "Connected to bot: " + id
    }


    fun mainAttack(){
        hpBar.progress =  hpBar.progress - 10


    }

    fun sideAttack(){
        hpBar.progress =  hpBar.progress - 5

    }

    fun gameOver(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, GameOverFragment()).addToBackStack("Welcome").commit()
    }

    fun update() {
        Log.d("Updated", "got updated; ")
        try {
            if (hpBar.progress == 0) {
                gameOver()
                hpBar.progress = 100;
                gameLoop.endLoop();
            }
        }
        catch (e: Exception) {
            gameLoop.endLoop();
            e.printStackTrace()
        }
    }


}