package com.xlab.findcar.model

data class Car(
    val id: Long = 0,
    val name: String,
    val depart: String,
    val phone: String,
    val plateNumber: String,
    val model: String,
    val image: String? = null
) 