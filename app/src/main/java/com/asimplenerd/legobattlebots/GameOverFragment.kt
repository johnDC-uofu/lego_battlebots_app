package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.game_over.*

class GameOverFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_over, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val playAgainBtn = this.view!!.findViewById<Button>(R.id.playAgainBtn)
        val menuBtn = this.view!!.findViewById<Button>(R.id.menuBtn)
        playAgainBtn.setOnClickListener(this)
        menuBtn.setOnClickListener(this)


        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.playAgainBtn -> showConnectionFrag()
            R.id.menuBtn -> showWelcomeFrag()
        }
    }

    fun updateUserName(name : String)
    {
        loggedInText4.text = "Logged in as: " + name
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

    fun showWelcomeFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
    }


}