package com.example.calvinrecipe

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.calvinrecipe.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    //View Binding
    private lateinit var binding: ActivitySignUpBinding

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var name = ""
    private var email = ""
    private var confirmemail = ""
    private var password = ""
    private var confirmpassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //signup button
        binding.signupBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        confirmemail = binding.confirmemailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        confirmpassword = binding.confirmpasswordEt.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid email format"
        }
        else if (confirmemail != email){
            binding.confirmemailEt.error = "Your email does not match"
        }
        //if password is empty
        else if (TextUtils.isEmpty(password)){
            binding.passwordEt.error = "Password cannot be empty"
        }
        else if (password.length < 6){
            binding.passwordEt.error = "Password must be at least 6 characters long"
        }
        else if (confirmpassword != password){
            binding.confirmpasswordEt.error = "Your password does not match"
        }
        //if data is validated
        else{
            firebaseSignup()
        }
    }

    private fun firebaseSignup() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Created an account with the email $email", Toast.LENGTH_SHORT).show()

                //open profile activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //signup failed
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity on back click
        return super.onSupportNavigateUp()
    }
}