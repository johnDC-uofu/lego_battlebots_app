package com.asimplenerd.legobattlebots

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.login_fragment.*


class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var database: DatabaseReference
    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        database = Firebase.database.reference
        val registerBtn = this.view!!.findViewById<Button>(R.id.registerBtn)
        val loginBtn = this.view!!.findViewById<Button>(R.id.logUserBtn)
        registerBtn.setOnClickListener(this)
        loginBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.registerBtn -> registerUser()
            R.id.loginBtn -> loginUser()
        }
    }

    private fun writeNewUser(user: User, userId: String, email: String, password: String, wins: String, losses: String) {
        database.child("users").child(userId).child("email").child("password").child("wins").child("losses").setValue(user)
    }

    fun registerUser()
    {
        if(emailInput.text.isEmpty()){
            Log.d("error","Enter username/password to setup")
        }
        else
        {
            val uEmail = emailInput.text.toString()
            val pass = passwordInput.text.toString()
            val uId = "100"
            val user = User(uEmail, pass, "0", "0")
            writeNewUser(user, uId, uEmail, pass,"0","0")

            MainActivity.username = uEmail

            val fragMan = this.parentFragmentManager
            fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
        }
    }

    fun loginUser()
    {
        if(emailInput.text.isEmpty()){
            Log.d("error","Enter username/password to login")
        }
        else
        {
            Log.d("error","Log in to database pls")
            val uEmail = emailInput.text.toString()
            val pass = passwordInput.text.toString()
            //TODO: read from database
            //val user = User(uEmail, pass, "0", "0")
            //writeNewUser(user, uId, uEmail, pass,"0","0")


            loggedInText.text = "Logged in as: " + uEmail

            val fragMan = this.parentFragmentManager
            fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
        }
    }


 
}
