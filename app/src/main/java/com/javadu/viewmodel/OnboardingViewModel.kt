package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    fun markOnboardingShown() {
        sharedPrefs.isOnboardingShown = true
    }
}
