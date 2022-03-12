package com.example.calvinrecipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.example.calvinrecipe.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityMainBinding

    //Action Bar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*var database = FirebaseDatabase.getInstance().reference

        database.setValue("Change")*/

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.addRecipeBtn.setOnClickListener {
            startActivity(Intent(this, SaveActivity::class.java))
        }
    }

    private fun checkUser() {
        //check if user is logged in
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null ){
            //if user is logged in, get info
            val email = firebaseUser.email

            //set email to action bar title
            actionBar = supportActionBar!!
            actionBar.title = email
        }
        else {
            //if user is not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.mMyRecipe -> startActivity(Intent(this, MyRecipeActivity::class.java))
            R.id.mLogout -> startActivity(Intent(this, MyRecipeActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}