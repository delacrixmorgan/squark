package com.delacrixmorgan.squark.data.controller

import com.delacrixmorgan.squark.App
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.getJsonMap
import com.delacrixmorgan.squark.data.model.Country
import java.util.*

object CountryDataController {
    private var countries = listOf<Country>()

    var countryMap = mapOf<String, String>()
    var currencyMap = mapOf<String, String>()

    init {
        countryMap = App.appContext.getJsonMap(R.raw.data_country, "currencies")
        currencyMap = App.appContext.getJsonMap(R.raw.data_currency, "quotes")
    }

    fun getCountryByCode(code: String?): Country? {
        return countries.firstOrNull { it.code == code }
    }

    fun getCountries() = countries

    fun updateDataSet(countries: List<Country>) {
        this.countries = countries
    }

    fun getFilteredCountries(
        searchText: String?
    ) = if (searchText.isNullOrBlank()) {
        getCountries()
    } else {
        val text: String = searchText.toLowerCase(Locale.US)
        getCountries().filter {
            it.name.toLowerCase(Locale.US).contains(text)
                    || it.code.toLowerCase(Locale.US).contains(text)
        }
    }
}