package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.PassportCountry

interface PassportRepository {
    fun getCountries(): List<PassportCountry>
    fun getCountryById(id: String): PassportCountry?
}