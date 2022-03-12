package com.example.calvinrecipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calvinrecipe.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //to animate splash
        binding.splashlogo.alpha = 0f
        binding.splashlogo.animate().setDuration(1500).alpha(1f).withEndAction{

            //to login page
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}