package com.example.calvinrecipe

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.calvinrecipe.databinding.ActivityReadBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ReadActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityReadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle : Bundle?= intent.extras

        //getting data from intent
        val recipeId = bundle!!.getString("recipeId")
        val recipeName = bundle.getString("recipeName")
        val ingredients = bundle.getString("ingredients")
        val cuisine = bundle.getString("cuisine")
        val recipe = bundle.getString("recipe")
        val email = bundle.getString("email")

        //setting data to view
        binding.recipeNameTvRead.text = recipeName
        binding.cuisineTvRead.text = cuisine
        binding.emailTvRead.text = email
        binding.ingredientsTvRead.text = ingredients
        binding.recipeDescTvRead.text = recipe

        //getting and setting image to view
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$recipeId.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.recipeIvRead.setImageBitmap(bitmap)
        }
    }
}