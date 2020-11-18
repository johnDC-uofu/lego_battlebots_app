package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.asimplenerd.legobattlebots.WeaponSelectFragment.Companion.attackId
import kotlinx.android.synthetic.main.play_screen.*

class Game(attackId: String) : Fragment(), View.OnClickListener, Joystick.JoystickListener {

    companion object {
        val botId = "100"
        var joystickPosX = "0"
        var joystickPosY = "0"
        var attacks = 0;

    }


    val armorLoop = GameLoop(this, 100)
    val outputLoop = GameLoop(this, 10)

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
        armorLoop.startLoop(false)
        outputLoop.startLoop(true)
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
        attacks += 1
    }

    fun sideAttack(){
    }

    fun gameOver(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, GameOverFragment()).addToBackStack("Welcome").commit()
    }

    fun update(output : Boolean) {

        if(output)
        {
            //Log.d("Updated", "output; ")
            try {
                val data = "Move Direction=" + MainActivity.direction + ", Speed=" + MainActivity.speed + " /n Attack Code=" + attackId + ", Amount= " + attacks + " end"
                toBluetooth(data)
                attacks = 0;
            } catch (e: Exception) {
                outputLoop.endLoop();
                e.printStackTrace()
            }
        }
        else {
            //Log.d("Updated", "armor; ")
            try {
                if (hpBar.progress == 0) {
                    gameOver()
                    hpBar.progress = 100;
                    armorLoop.endLoop();
                }
            } catch (e: Exception) {
                armorLoop.endLoop();
                e.printStackTrace()
            }
        }
    }

    fun toBluetooth(data : String){
        //TODO : send this data to device through bluetooth
        Log.d("To Bluetooth", data);
    }

    override fun onJoystickMoved(xPercent: Float, yPercent: Float, id: Int) {
        joystickPosX = xPercent.toString()
        joystickPosY = yPercent.toString()

        Log.d("To Bluetooth", "GOT HERE GOT HERE");
    }


}