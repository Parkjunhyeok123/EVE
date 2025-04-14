package com.company.eve.ui.home.kotlintest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "빠른 길찾기"
    }
    val text: LiveData<String> = _text
}
