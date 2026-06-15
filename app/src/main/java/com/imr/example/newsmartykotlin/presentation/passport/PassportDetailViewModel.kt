package com.imr.example.newsmartykotlin.presentation.passport

import androidx.lifecycle.ViewModel
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository

class PassportDetailViewModel(
    private val repository: PassportRepository
) : ViewModel() {

    fun getCountry(id: String): PassportCountry? {
        return repository.getCountryById(id)
    }
}