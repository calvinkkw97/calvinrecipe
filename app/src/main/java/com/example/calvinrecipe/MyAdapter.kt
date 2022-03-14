package com.example.calvinrecipe

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MyAdapter(private var recipeList : ArrayList<Recipes>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{

        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item,
            parent, false)

        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = recipeList[position]

        holder.recipeName.text = currentitem.recipename
        holder.cusineType.text = currentitem.recipetype
        holder.email.text = currentitem.email

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

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val recipeName : TextView = itemView.findViewById(R.id.recipeNameTvMain)
        val cusineType : TextView = itemView.findViewById(R.id.cuisineMainTv)
        val recipeImage : ImageView = itemView.findViewById(R.id.recipeIvMain)
        val email : TextView = itemView.findViewById(R.id.emailTvMain)

        init {
            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

}