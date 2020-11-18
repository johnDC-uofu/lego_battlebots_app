package com.asimplenerd.legobattlebots

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_welcome.*


class WelcomeFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = WelcomeFragment()
        //val vibe = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val battleBtn = this.view!!.findViewById<Button>(R.id.battleBtn) 
        val loginBtn = this.view!!.findViewById<Button>(R.id.loginBtn)
        battleBtn.setOnClickListener(this)
        loginBtn.setOnClickListener(this)
        updateUserName(MainActivity.username)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        buttonEffect(v)
       // vibe.vibrate(80)
        //vibe.vibrate(VibrationEffect.createOneShot(80L,1))
        when(v?.id){
            R.id.battleBtn -> showConnectionFrag()
            R.id.loginBtn -> showLoginFrag()
           // R.id.connectBtn -> showGameFrag()
           // R.id.goBackBtn -> showLoginFrag()
        }
    }

    fun updateUserName(name : String)
    {
        loggedInText.text = "Logged in as: " + name
    }

    fun showConnectionFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, ConnectionFragment()).addToBackStack("Connect").commit()
    }
//
//    fun showGameFrag(){
//        val fragMan = this.parentFragmentManager
//        fragMan.beginTransaction().replace(R.id.interactionFragment, Game()).addToBackStack("Game").commit()
//    }

    fun showLoginFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, LoginFragment()).addToBackStack("Welcome").commit()
    }

    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.setBackgroundColor(-0x1f0b8adf)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.setBackgroundColor(0)
                    v.invalidate()
                }
            }
            false
        }
    }

}
