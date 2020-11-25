package com.asimplenerd.legobattlebots

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.asimplenerd.legobattlebots.WeaponSelectFragment.Companion.attackId
import kotlinx.android.synthetic.main.play_screen.*

class Game(attackId: String) : Fragment(), View.OnClickListener {

    companion object {

        val MAX_ARMOR_LEVEL = 3
        val botId = ConnectionFragment.botId
        var joystickPosX = "0"
        var joystickPosY = "0"
        var attacks = 0
        var armor = MAX_ARMOR_LEVEL

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

        speedBar.progress = 0
        super.onActivityCreated(savedInstanceState)
        val attackBtn1 = this.view!!.findViewById<Button>(R.id.attackBtn1)
        val attackBtn2 = this.view!!.findViewById<Button>(R.id.attackBtn2)
        attackBtn1.setOnClickListener(this)
        attackBtn2.setOnClickListener(this)
        attackBtn1.text = attackId

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
        //Disconnect from the bot
        ConnectionFragment.battleBot?.disconnect()
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, GameOverFragment()).addToBackStack("Welcome").commit()
    }

    fun update(output : Boolean) {

        if(output)
        {
            //Log.d("Updated", "output; ")
            try {
                toBluetooth(MainActivity.xPos, MainActivity.yPos, attackId!!, attacks.toString())
                attacks = 0;
            } catch (e: Exception) {
                outputLoop.endLoop();
                e.printStackTrace()
            }
        }
        else {
            //Log.d("Updated", "armor; ")
            try {

                //fromBluetooth()



                val percent = armor.toFloat() / MAX_ARMOR_LEVEL.toFloat() * 100.0F
                Log.d("Percent: ", percent.toString())
                hpBar.progress = percent.toInt()

                if (hpBar.progress == 0) {
                    gameOver()
                    hpBar.progress = 100
                    armor = MAX_ARMOR_LEVEL
                    armorLoop.endLoop()
                    outputLoop.endLoop()
                }
            } catch (e: Exception) {
                armorLoop.endLoop();
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromBluetooth() {
        //TODO : receive this data to device through bluetooth, this random armor decrement is for testing purposes only

        val randomNum = (0..50).random() //1 in 50 chance the armor will be decremented every 100ms

        if(randomNum == 10)
        {
            armor -= 1
            //VibrationEffect.createOneShot(1000,100)
            VibrationEffect.EFFECT_HEAVY_CLICK
        }

        Log.d("From Bluetooth", "armor set to: " + armor)
    }

    fun toBluetooth(xCoord : String, yCoord : String, weapon : String, attackAmt : String){
        //TODO : send this data to device through bluetooth


        val data =
            "Joystick Position: $xCoord,$yCoord /n Attack Weapon=$weapon, Amount= $attackAmt end"

        Log.d("To Bluetooth", data)
    }


}