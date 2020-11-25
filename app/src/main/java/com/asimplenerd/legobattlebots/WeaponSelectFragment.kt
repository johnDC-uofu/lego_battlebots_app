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
import kotlinx.android.synthetic.main.fragment_connection.loggedInText3
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.select_weapon.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class WeaponSelectFragment : Fragment(), View.OnClickListener  {

    companion object {
        var attackId : String? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.select_weapon, container, false)
        val manager = activity!!.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter
        //adapter.startDiscovery()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val goBattleBtn = this.view!!.findViewById<Button>(R.id.goBattleBtn)
        val goBackBtn = this.view!!.findViewById<Button>(R.id.goBackBtn)
        val weaponSelectBtn1 = this.view!!.findViewById<Button>(R.id.weaponSelectBtn1)
        val weaponSelectBtn2 = this.view!!.findViewById<Button>(R.id.weaponSelectBtn2)
        val weaponSelectBtn3 = this.view!!.findViewById<Button>(R.id.weaponSelectBtn3)
        goBackBtn.setOnClickListener(this)
        weaponSelectBtn1.setOnClickListener(this)
        weaponSelectBtn2.setOnClickListener(this)
        weaponSelectBtn3.setOnClickListener(this)

        goBattleBtn.visibility = View.INVISIBLE


        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {

        //AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_animator);
        // AnimationUtils.loadAnimation(MainActivity.,R.anim.button_animator)

        when(v?.id){
            R.id.goBattleBtn -> showGameFrag()
            R.id.goBackBtn -> goBackFrag()

            R.id.weaponSelectBtn1 -> {
                selectWeapon("Sword")
            }
            R.id.weaponSelectBtn2 -> {
                selectWeapon("Axe")
            }
            R.id.weaponSelectBtn3 -> {
                selectWeapon("Lifter")
            }
        }
    }


    fun updateUserName(name : String)
    {
        loggedInText3.text = "Logged in as: " + name
    }

    fun selectWeapon(weaponName : String)
    {
        attackId = weaponName
        goBattleBtn.setOnClickListener(this)
        weaponSelectedText.text = "Weapon Selected: $weaponName"
        goBattleBtn.visibility = View.VISIBLE
        ConnectionFragment.battleBot?.setWeapon(weaponName)
    }



    fun showGameFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, Game(attackId!!)).addToBackStack("Game").commit()
    }

    fun goBackFrag(){
        ConnectionFragment.battleBot?.disconnect()
        MainActivity.startScan()
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, ConnectionFragment()).addToBackStack("Welcome").commit()
    }

}
