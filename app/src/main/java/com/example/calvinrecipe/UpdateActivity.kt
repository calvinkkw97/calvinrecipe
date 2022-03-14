package com.example.calvinrecipe

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.calvinrecipe.databinding.ActivityUpdateBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UpdateActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding : ActivityUpdateBinding

    //db ref
    private lateinit var db : DatabaseReference

    private lateinit var filepath : Uri

    private var cuisinevalue = ""
    private var gotPhotoToUpdate= "no"
    private var passedemail = ""
    private var recipeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get val passed from previous activity
        val bundle : Bundle ?= intent.extras

        recipeId = bundle!!.getString("recipeId").toString()
        val recipeName = bundle.getString("recipeName")
        val ingredients = bundle.getString("ingredients")
        val cuisine = bundle.getString("cuisine")
        val recipe = bundle.getString("recipe")
        val email = bundle.getString("email")

        passedemail = email.toString().trim()

        //assign val to edit text
        binding.updaterecipeNameEt.setText(recipeName)
        binding.updateingredientsEt.setText(ingredients)
        binding.updaterecipeEt.setText(recipe)

        //set value to spinner
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.cuisines,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.updateSpinnerRecipeType.adapter =arrayAdapter

        binding.updateSpinnerRecipeType.setSelection(arrayAdapter.getPosition(cuisine))

        cuisinevalue = cuisine.toString().trim()

        //on spinner item selected
        binding.updateSpinnerRecipeType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0!!.getItemAtPosition(p2)
                cuisinevalue = selectedItem.toString().trim()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //get photo
        val storageref = FirebaseStorage.getInstance().reference.child("images/$recipeId.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageref.getFile(localfile).addOnSuccessListener {

            //set photo to imageview
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.updatephotoIv.setImageBitmap(bitmap)
        }

        binding.updatephotoIv.setOnClickListener {
            selectImage()
        }

        binding.updateBtn.setOnClickListener {
            updateRecipe()
        }

        binding.deleteBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes", object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        deleteRecipe()
                    }
                })
                .setNegativeButton("No"
                ) { p0, p1 -> p0.dismiss() }
                .show()
        }
    }

    private fun deleteRecipe() {
        //remove from database
        db = FirebaseDatabase.getInstance().reference.child("recipes/$recipeId")
        db.removeValue().addOnSuccessListener {
            //on success, delete photo from storage
            val imageref = FirebaseStorage.getInstance().reference.child("images/$recipeId.jpg")
            imageref.delete().addOnSuccessListener {
                //after image is deleted
                Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MyRecipeActivity::class.java))
                finish()
            }
        }


    }

    private fun updateRecipe() {
        var recipeName : String = ""
        var recipe : String = ""
        var ingredient : String = ""

        recipeName = binding.updaterecipeNameEt.text.toString().trim()
        recipe = binding.updaterecipeEt.text.toString().trim()
        ingredient = binding.updateingredientsEt.text.toString().trim()

        //validate data
        if (cuisinevalue == "All Cuisines") {
            Toast.makeText(this, "Please Select A Cuisine Type", Toast.LENGTH_LONG).show()
        }
        else if (TextUtils.isEmpty(recipeName)){
            binding.updaterecipeNameEt.error = "Recipe name cannot be empty"
        }
        else if (recipeName.length < 6){
            binding.updaterecipeNameEt.error = "Recipe name cis too short"
        }
        else if(TextUtils.isEmpty(ingredient)){
            binding.updateingredientsEt.error = "Ingredients cannot be empty"
        }
        else if (ingredient.length < 6){
            binding.updateingredientsEt.error = "Ingredients description is too short"
        }
        else if(TextUtils.isEmpty(recipe)){
            binding.updaterecipeEt.error = "Recipe cannot be empty"
        }
        else if(recipe.length < 6){
            binding.updaterecipeEt.error = "Recipe description is too short"
        }
        else {
            //if everything is ok save to db
            db = FirebaseDatabase.getInstance().getReference("recipes")

            val toUpdateRecipe = mapOf<String,String>(
                "recipename" to recipeName,
                "recipetype" to cuisinevalue,
                "ingredients" to ingredient,
                "recipe" to recipe
            )
            db.child(recipeId).updateChildren(toUpdateRecipe).addOnSuccessListener {
                saveImage()
            }
        }
    }

    //if got photo to update
    private fun saveImage() {
        if (gotPhotoToUpdate == "no"){
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        }
        else{
            //delete photo from storage
            val imageref = FirebaseStorage.getInstance().reference.child("images/$recipeId.jpg")

            imageref.delete().addOnSuccessListener {
                if (filepath != null){
                    imageref.putFile(filepath).addOnSuccessListener {
                        Toast.makeText(this, "Saved with image", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun selectImage() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            binding.updatephotoIv.setImageBitmap(bitmap)

            gotPhotoToUpdate = "yes"
        }
    }
}