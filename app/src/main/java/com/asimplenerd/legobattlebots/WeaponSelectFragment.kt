package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_connection.loggedInText3
import kotlinx.android.synthetic.main.select_weapon.*

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
        return inflater.inflate(R.layout.select_weapon, container, false)
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


    private fun updateUserName(name : String)
    {
        loggedInText3.text = getString(R.string.logged_in_user, name)
    }

    private fun selectWeapon(weaponName : String)
    {
        attackId = weaponName
        goBattleBtn.setOnClickListener(this)
        weaponSelectedText.text = getString(R.string.weapon_selected, weaponName)
        goBattleBtn.visibility = View.VISIBLE
        ConnectionFragment.battleBot?.setWeapon(weaponName)
    }



    private fun showGameFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, Game()).commit()
    }

    private fun goBackFrag(){
        ConnectionFragment.battleBot?.disconnect()
        MainActivity.startScan()
        val fragMan = this.parentFragmentManager
        fragMan.popBackStack("Connection", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}
