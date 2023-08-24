package com.nurtore.imam_ai

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    // private mutable list so that it cant be modified from other places
    @SuppressLint("MutableCollectionMutableState")
    private val _messagesList = mutableStateListOf<String>("niga")

    val messagesList: List<String> = _messagesList

    private var x = 0;
    fun sendMessage() {
        println("pressed")
        _messagesList.add("button pressed bro" + x)
        x++;
        println(messagesList)
    }
}