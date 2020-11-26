package com.asimplenerd.legobattlebots

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_fragment.*


class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser :FirebaseUser?)
    {
        Log.d("CURRUSR", currentUser.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        database = Firebase.database.reference
        val registerBtn = this.view!!.findViewById<Button>(R.id.registerBtn)
        val loginBtn = this.view!!.findViewById<Button>(R.id.loginBtn)
        val gotoWelcomeBtn = this.view!!.findViewById<Button>(R.id.gotoWelcomeBtn)
        registerBtn.setOnClickListener(this)
        loginBtn.setOnClickListener(this)
        gotoWelcomeBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.registerBtn -> registerUser()
            R.id.loginBtn -> loginUser()
            R.id.gotoWelcomeBtn -> goBack()
        }
    }

    private fun writeNewUser(user: User, userId: String, email: String, password: String, wins: String, losses: String) {
        database.child("users").child(userId).child("email").child("password").child("wins").child("losses").setValue(user)
    }

    private fun goBack(){

        val fragMan = this.parentFragmentManager
        fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
    }

   private fun registerUser()
    {
        if(emailInput.text.isEmpty()){
            emailInput.error = "Please enter email"
            emailInput.requestFocus()
            return
        }
        else if (passwordInput.text.isEmpty()){
            passwordInput.error = "Please enter password"
            passwordInput.requestFocus()
            return
        }
        else if (passwordInput.text.length < 6){
            passwordInput.error = "Password must be 6 characters or longer"
            passwordInput.requestFocus()
            return
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()){
            emailInput.error = "Please enter a valid email address"
            emailInput.requestFocus()
            return
        }
        else
        {
            auth.createUserWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)

                        val uEmail = emailInput.text.toString()
                        val pass = passwordInput.text.toString()
                        val uId = "100"
                        val newUser = User(uEmail, pass, "0", "0")
                        writeNewUser(newUser, uId, uEmail, pass,"0","0")

                        MainActivity.username = uEmail

                        val fragMan = this.parentFragmentManager
                        fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // ...
                }


        }
    }

    @Suppress("SameParameterValue")
    private fun loginUser()
    {

        if(emailInput.text.isEmpty()){
            emailInput.error = "Please enter email"
            emailInput.requestFocus()
            return
        }
        else if (passwordInput.text.isEmpty()){
            passwordInput.error = "Please enter password"
            passwordInput.requestFocus()
            return
        }
        else if (passwordInput.text.length < 6){
            passwordInput.error = "Password must be 6 characters or longer"
            passwordInput.requestFocus()
            return
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()){
            emailInput.error = "Please enter a valid email address"
            emailInput.requestFocus()
            return
        }
        else
        {
            auth.signInWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString())
                .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            //updateUI(user)

                            MainActivity.username = emailInput.text.toString()
                            val fragMan = this.parentFragmentManager
                            fragMan.beginTransaction().replace(R.id.interactionFragment, WelcomeFragment()).addToBackStack("Welcome").commit()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(null)
                        }

                        // ...
                    }

        }
    }




 
}
