package com.example.calvinrecipe

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UserAdapter( private var recipeList : ArrayList<Recipes>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class UserViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val recipeName : TextView = itemView.findViewById(R.id.recipeNameTvUser)
        val cusineType : TextView = itemView.findViewById(R.id.cuisineUserTv)
        val recipeImage : ImageView = itemView.findViewById(R.id.recipeIvUser)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.useritem,
            parent, false)

        return UserViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentitem = recipeList[position]

        holder.recipeName.text = currentitem.recipename
        holder.cusineType.text = currentitem.recipetype

        val imageid = currentitem.id

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageid.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.recipeImage.setImageBitmap(bitmap)
        }
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }
}