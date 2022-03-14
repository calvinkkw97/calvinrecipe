package com.example.calvinrecipe

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calvinrecipe.databinding.ActivityMyRecipeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyRecipeActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityMyRecipeBinding

    //Action Bar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //RecyclerView init
    private lateinit var recipeRecyclerView : RecyclerView

    //Array
    private lateinit var recipeArrayList : ArrayList<Recipes>

    //email val
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //recyclerview setting
        recipeRecyclerView = binding.userRecyclerV
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        recipeRecyclerView.setHasFixedSize(true)

        recipeArrayList = arrayListOf<Recipes>()

        getUserRecipeData()

    }


    private fun getUserRecipeData() {

        //check if user is logged in
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null ){
            //if user is logged in, get info
            userEmail = firebaseUser.email.toString()

            //set email to action bar title
            actionBar = supportActionBar!!
            actionBar.title = userEmail
        }
        else {
            //if user is not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //querying database
        val queries = FirebaseDatabase.getInstance().getReference("recipes")
            .orderByChild("email").equalTo("$userEmail")

        recipeArrayList.clear()
        recipeRecyclerView.adapter = MyAdapter(recipeArrayList)

        queries.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (recipeSnapshot in snapshot.children){

                        val recipe = recipeSnapshot.getValue(Recipes::class.java)
                        recipeArrayList.add(recipe!!)

                    }
                    recipeArrayList.reverse()
                    var adapter = UserAdapter(recipeArrayList)
                    recipeRecyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object: UserAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@MyRecipeActivity, UpdateActivity::class.java)

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
        if(firebaseUser == null ){
            //if user is not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    //inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    //on actionbar item menu selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.mAllRecipe -> startActivity(Intent(this, MainActivity::class.java))
            R.id.mLogout -> {firebaseAuth.signOut()
                            checkUser()}
        }
        finish()
        return super.onOptionsItemSelected(item)
    }

}