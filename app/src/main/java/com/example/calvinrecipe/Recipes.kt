package com.example.calvinrecipe

data class Recipes(
    val id: String? = null,
    val recipename: String? = null,
    val ingredients : String? = null,
    val recipe: String? = null,
    val recipetype: String? = null,
    val email: String?= null,
    val timestamp: String ?= null
){
    constructor():this("","","","","","", "")
}