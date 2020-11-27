package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.game_over.*

class GameOverFragment : Fragment(), View.OnClickListener {

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

        ConnectionFragment.battleBot?.exitCombat()

        updateUserName(MainActivity.username)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.playAgainBtn -> showConnectionFrag()
            R.id.menuBtn -> showWelcomeFrag()
        }
    }

    private fun updateUserName(name : String)
    {
        loggedInText4.text = getString(R.string.logged_in_user, name)
    }


    private fun showConnectionFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.popBackStack("Connection", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showWelcomeFrag(){
        val fragMan = this.parentFragmentManager
        fragMan.popBackStack("Welcome", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }


}