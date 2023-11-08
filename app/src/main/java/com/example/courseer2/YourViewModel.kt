// YourViewModel.kt

package com.example.courseer2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class YourViewModel : ViewModel() {
    // Define LiveData for user data
    private val _userData = MutableLiveData<Map<String, String>>()
    val userData: LiveData<Map<String, String>> get() = _userData

    // Method to update user data
    fun updateUserData(userData: Map<String, String>) {
        _userData.value = userData
    }
}

