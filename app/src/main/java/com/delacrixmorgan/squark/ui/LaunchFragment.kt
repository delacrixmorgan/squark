package com.delacrixmorgan.squark.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.SharedPreferenceHelper
import com.delacrixmorgan.squark.common.getJsonMap
import com.delacrixmorgan.squark.data.api.SquarkResult
import com.delacrixmorgan.squark.data.controller.CountryDataController
import com.delacrixmorgan.squark.data.dao.CountryDataDao
import com.delacrixmorgan.squark.data.dao.CountryDatabase
import com.delacrixmorgan.squark.data.model.Country
import com.delacrixmorgan.squark.data.model.Currency
import com.delacrixmorgan.squark.data.service.SquarkService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_launch.*
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault
import java.util.*
import java.util.concurrent.TimeUnit

@UnstableDefault
class LaunchFragment : Fragment() {
    private var countryDatabaseDao: CountryDataDao? = null
    private var countries = listOf<Country>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            countryDatabaseDao = CountryDatabase.getInstance(requireContext())?.countryDataDao()
            countries = countryDatabaseDao?.getCountries() ?: listOf()

            if (!countries.isNullOrEmpty()) {
                populateCountries()
            } else {
                fetchCountries()
            }
        }
    }

    private fun populateCountries() {
        val lastUpdatedDateTime = SharedPreferenceHelper.lastUpdatedDate.time
        val currentDateTime = Date().time

        if (TimeUnit.MILLISECONDS.toDays(currentDateTime - lastUpdatedDateTime) >= 1) {
            updateCurrencyRates()
        } else {
            CountryDataController.updateDataSet(countries)
            launchCurrencyNavigationFragment()
        }
    }

    override fun onDestroy() {
        CountryDatabase.destroyInstance()
        super.onDestroy()
    }

    private fun fetchCountries() {
        lifecycleScope.launch {
            val currencies = fetchCurrencies() ?: fetchFallbackCurrencies()
            addCountryDatabase(currencies)
        }
    }

    private suspend fun fetchCurrencies(): List<Currency>? {
        return when (val result = SquarkService.getCurrencies()) {
            is SquarkResult.Success -> {
                result.value.currencies
            }
            is SquarkResult.Failure -> {
                Snackbar.make(
                    mainContainer,
                    result.error.localizedMessage ?: "",
                    Snackbar.LENGTH_SHORT
                ).show()
                null
            }
        }
    }

    private fun updateCurrencyRates() {
        lifecycleScope.launch {
            when (val result = SquarkService.getCurrencies()) {
                is SquarkResult.Success -> {
                    updateCurrencies(result.value.currencies)
                }
                is SquarkResult.Failure -> {
                    val currencies = fetchFallbackCurrencies()
                    addCountryDatabase(currencies)

                    Snackbar.make(
                        mainContainer,
                        getString(R.string.error_api_countries),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchFallbackCurrencies(): List<Currency> {
        return requireContext().getJsonMap(R.raw.data_currency, "quotes")
            .map { Currency(code = it.key, rate = it.value.toDouble()) }
    }

    private suspend fun addCountryDatabase(currencies: List<Currency>) {
        val countries = CountryDataController.countryMap.mapNotNull { country ->
            val currency = currencies.firstOrNull { it.code == "USD${country.key}" }
            Country(code = country.key, name = country.value, rate = currency?.rate ?: 0.0)
        }

        countries.forEach { countryDatabaseDao?.insertCountry(it) }
        CountryDataController.updateDataSet(countries)

        SharedPreferenceHelper.lastUpdatedDate = Date()
        launchCurrencyNavigationFragment()
    }

    private suspend fun updateCurrencies(currencies: List<Currency>) {
        countries.forEach { country ->
            currencies.firstOrNull { it.code == country.code }?.let {
                country.rate = it.rate
                countryDatabaseDao?.updateCountry(country)
            }
        }

        if (isVisible) {
            Snackbar.make(
                mainContainer,
                getString(R.string.fragment_country_list_title_updated),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        SharedPreferenceHelper.lastUpdatedDate = Date()
        CountryDataController.updateDataSet(countries)
        populateCountries()
    }

    private fun launchCurrencyNavigationFragment() {
        val action = LaunchFragmentDirections.actionLaunchFragmentToCurrencyNavigationFragment()
        Navigation.findNavController(rootView).navigate(action)
    }
}