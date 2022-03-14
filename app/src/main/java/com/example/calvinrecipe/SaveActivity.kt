package com.example.calvinrecipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.calvinrecipe.databinding.ActivitySaveBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage

class SaveActivity : AppCompatActivity() {
    //View Binding
    private lateinit var binding: ActivitySaveBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var filepath : Uri

    private var cuisinevalue = ""

    private var gotphoto = "no"

    private var useremail = ""

    private var recId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting aray into spinner
        val arrayAdapter = ArrayAdapter.createFromResource(this,R.array.cuisines,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerAddRecipeType.adapter = arrayAdapter


        //when spinner item is selected
        binding.spinnerAddRecipeType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0!!.getItemAtPosition(p2)
                cuisinevalue = selectedItem.toString().trim()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }

        binding.addphotoIv.setOnClickListener {
            selectImage()
        }

        binding.saveBtn.setOnClickListener {
            saveData()
        }
    }

    //to choose image
    private fun selectImage() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"),111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            binding.addphotoIv.setImageBitmap(bitmap)
            gotphoto = "yes"
        }
    }


    private fun saveData(){

        var recipeName : String = ""
        var recipe : String = ""
        var ingredient : String = ""

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        //if not null set TV as email
        if(firebaseUser != null ){
            useremail = firebaseUser.email.toString().trim()
        }else
        {
            //nothing
        }

        //get data from edit text
        recipeName = binding.recipeNameEt.text.toString().trim()
        recipe = binding.recipeEt.text.toString().trim()
        ingredient = binding.ingredientsEt.text.toString().trim()


        //validate data
        if (cuisinevalue == "All Cuisines"){
            Toast.makeText(this, "Please Select A Cuisine Type", Toast.LENGTH_LONG).show()
        }
        else if(gotphoto == "no"){
            Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_LONG).show()
        }
        else if(TextUtils.isEmpty(recipeName)){
            binding.recipeNameEt.error = "Recipe Name cannot be empty"
        }
        else if (recipeName.length < 6){
            binding.recipeNameEt.error = "Recipe Name must be more than 6 characters"
        }
        else if(TextUtils.isEmpty(recipe)){
            binding.recipeEt.error = "Recipe Name cannot be empty"
        }
        else if (recipeName.length < 6){
            binding.recipeEt.error = "Recipe Name must be more than 6 characters"
        }
        else{


            //save to firebase
            var database = FirebaseDatabase.getInstance().getReference("recipes")

            val recipeId = database.push().key

            val rec = Recipes(recipeId, recipeName, ingredient,  recipe, cuisinevalue, useremail)

            recId = recipeId.toString().trim()


            if (recipeId != null) {
                //push to firebase
                database.child(recipeId).setValue(rec).addOnCompleteListener {
                    //when data saved, save image to storage
                    saveImage()
                }
            }
            else{
                Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show()
            }
            clearText()
        }

    }

    //save image to storage
    private fun saveImage() {
        if (filepath != null){
            var imageRef = FirebaseStorage.getInstance().reference.child("images/$recId.jpg")
            imageRef.putFile(filepath).addOnSuccessListener {
                Toast.makeText(this, "Saved with image", Toast.LENGTH_LONG).show()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Saved without image", Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun clearText() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}