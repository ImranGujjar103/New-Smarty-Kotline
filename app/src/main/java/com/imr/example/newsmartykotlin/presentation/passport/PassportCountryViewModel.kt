package com.imr.example.newsmartykotlin.presentation.passport

import androidx.lifecycle.ViewModel
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PassportCountryViewModel(
    repository: PassportRepository
) : ViewModel() {

    private val allCountries = repository.getCountries()

    private val _countries = MutableStateFlow(allCountries)
    val countries: StateFlow<List<PassportCountry>> = _countries
    private val _selectedType = MutableStateFlow(DocumentType.ALL)
    val selectedType: StateFlow<DocumentType> = _selectedType

    fun onTypeClick(type: DocumentType) {
        _selectedType.value = type
    }
    fun search(query: String) {
        _countries.value = if (query.isBlank()) {
            allCountries
        } else {
            allCountries.filter {
                it.id.contains(query.lowercase().replace(" ", "_"))
            }
        }
    }
}