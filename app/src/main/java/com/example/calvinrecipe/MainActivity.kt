package com.example.calvinrecipe

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calvinrecipe.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    //View Binding
    private lateinit var binding: ActivityMainBinding

    //Action Bar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //RecyclerView init
    private lateinit var recipeRecyclerView : RecyclerView

    //Database Reference
    private lateinit var dbref : DatabaseReference

    //Array
    private lateinit var recipeArrayList : ArrayList<Recipes>

    //cuisine name
    private var cuisineValue = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //recyclerview setting
        recipeRecyclerView = binding.mainRecyclerV
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        recipeRecyclerView.setHasFixedSize(true)


        recipeArrayList = arrayListOf<Recipes>()

        //get all data oncreate
        getRecipeData()

        binding.addRecipeBtn.setOnClickListener {
            startActivity(Intent(this, SaveActivity::class.java))
        }

        //set up spinner
        val arrayAdapter = ArrayAdapter.createFromResource(this,R.array.cuisines,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerMain.adapter = arrayAdapter

        binding.spinnerMain.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if (p2 == 0){
                    getRecipeData()
                }
                else {
                    val selectedItem = p0!!.getItemAtPosition(p2)
                    cuisineValue = selectedItem.toString().trim()
                    filteredData()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                getRecipeData()
            }

        }
    }

    //if its not All Cuisines
    private fun filteredData() {
        val queries = FirebaseDatabase.getInstance().getReference("recipes")
            .orderByChild("recipetype").equalTo("$cuisineValue")

        recipeArrayList.clear()
        recipeRecyclerView.adapter = MyAdapter(recipeArrayList)

        queries.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (recipeSnapshot in snapshot.children){

                        val recipe = recipeSnapshot.getValue(Recipes::class.java)
                        recipeArrayList.add(recipe!!)

                    }
                    recipeArrayList.reverse()
                    var adapter = MyAdapter(recipeArrayList)
                    recipeRecyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object: MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            //intent to pass extra to next activity
                            val intent = Intent(this@MainActivity, ReadActivity::class.java)
                            intent.putExtra("recipeId", recipeArrayList[position].id)
                            intent.putExtra("recipeName", recipeArrayList[position].recipename)
                            intent.putExtra("ingredients", recipeArrayList[position].ingredients)
                            intent.putExtra("recipe", recipeArrayList[position].recipe)
                            intent.putExtra("cuisine", recipeArrayList[position].recipetype)
                            intent.putExtra("email", recipeArrayList[position].email)
                            startActivity(intent)

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    //if all cuisine is seleted
    private fun getRecipeData() {
        dbref = FirebaseDatabase.getInstance().getReference("recipes")

        recipeArrayList.clear()
        recipeRecyclerView.adapter = MyAdapter(recipeArrayList)

        dbref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeArrayList.clear()
                if (snapshot.exists()){
                    for (recipeSnapshot in snapshot.children){

                        val recipe = recipeSnapshot.getValue(Recipes::class.java)
                        recipeArrayList.add(recipe!!)

                    }
                    recipeArrayList.reverse()
                    var adapter = MyAdapter(recipeArrayList)
                    recipeRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: MyAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@MainActivity, ReadActivity::class.java)
                            intent.putExtra("recipeId", recipeArrayList[position].id)
                            intent.putExtra("recipeName", recipeArrayList[position].recipename)
                            intent.putExtra("ingredients", recipeArrayList[position].ingredients)
                            intent.putExtra("recipe", recipeArrayList[position].recipe)
                            intent.putExtra("cuisine", recipeArrayList[position].recipetype)
                            intent.putExtra("email", recipeArrayList[position].email)
                            startActivity(intent)

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun checkUser() {
        //check if user is logged in
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null ){
            //if user is logged in, get info
            val email = firebaseUser.email

            //set title to action bar title
            actionBar = supportActionBar!!
            actionBar.title = "All Recipes"
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

    //when menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.mMyRecipe -> startActivity(Intent(this, MyRecipeActivity::class.java))
            R.id.mLogout -> {firebaseAuth.signOut()
                            checkUser()}
        }
        finish()
        return super.onOptionsItemSelected(item)
    }
}