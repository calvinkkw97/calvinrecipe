package com.example.calvinrecipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.calvinrecipe.databinding.ActivitySaveBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SaveActivity : AppCompatActivity() {
    //View Binding
    private lateinit var binding: ActivitySaveBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrayAdapter = ArrayAdapter.createFromResource(this,R.array.cuisines,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerAddRecipeType.adapter = arrayAdapter

        binding.spinnerAddRecipeType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0!!.getItemAtPosition(p2)
                binding.result.text = "$selectedItem"
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }

        binding.saveBtn.setOnClickListener {
            saveData()
        }
    }

    private fun saveData(){

        var recipeName : String = ""
        var recipe : String = ""
        var spinnersel : String = ""
        var userEmail : String = ""

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        //if not null set TV as email
        if(firebaseUser != null ){
            val email = firebaseUser.email
            binding.emailTv.text = email
        }else
        {
            //nothing
        }


        recipeName = binding.recipeNameEt.text.toString().trim()
        recipe = binding.recipeEt.text.toString().trim()
        spinnersel = binding.result.text.toString().trim()
        userEmail = binding.emailTv.text.toString().trim()


        if (spinnersel == "All Cuisines"){
            Toast.makeText(this, "Please Select A Cuisine Type", Toast.LENGTH_LONG).show()
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

            val rec = Recipes(recipeId, recipeName, recipe, spinnersel, userEmail)

            binding.recipeId.setText("$recipeId")


            if (recipeId != null) {
                database.child(recipeId).setValue(rec).addOnCompleteListener {
                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
            clearText()
        }

    }

    private fun clearText() {
        binding.recipeNameEt.setText("")
        binding.recipeEt.setText("")
        binding.spinnerAddRecipeType.setSelection(0)
    }
}