package com.delacrixmorgan.squark.ui.currency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.Keys
import com.delacrixmorgan.squark.common.RowListener
import com.delacrixmorgan.squark.common.SharedPreferenceHelper
import com.delacrixmorgan.squark.common.performHapticContextClick
import com.delacrixmorgan.squark.data.controller.CountryDataController
import com.delacrixmorgan.squark.data.model.Country
import com.delacrixmorgan.squark.ui.preference.PreferenceNavigationActivity
import kotlinx.android.synthetic.main.fragment_currency_navigation.*

class CurrencyFragment : Fragment(), RowListener {

    companion object {
        private const val REQUEST_BASE_COUNTRY = 1
        private const val REQUEST_QUOTE_COUNTRY = 2
    }

    private var isExpanded = false
    private var baseCountry: Country? = null
        set(value) {
            field = value
            baseCurrencyTextView.text = value?.code
            quoteCurrencyTextView.text = quoteCountry?.code

        }
    private var quoteCountry: Country? = null
        set(value) {
            field = value
            quoteCurrencyTextView.text = value?.code
        }

    private var rowList = arrayListOf<TableRow>()
    private var expandedList = arrayListOf<TableRow>()

    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currency_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SharedPreferenceHelper.isPersistentMultiplierEnabled) {
            viewModel.updateMultiplier(SharedPreferenceHelper.multiplier)
        }

        viewModel.setupTable(
            activity = requireActivity(),
            tableLayout = currencyTableLayout,
            rowList = rowList,
            listener = this
        )

        baseCurrencyTextView.setOnClickListener {
            val currencyIntent = PreferenceNavigationActivity.create(
                view.context,
                countryCode = baseCountry?.code
            )
            startActivityForResult(
                currencyIntent,
                REQUEST_BASE_COUNTRY
            )
        }

        quoteCurrencyTextView.setOnClickListener {
            val currencyIntent = PreferenceNavigationActivity.create(
                view.context,
                countryCode = quoteCountry?.code
            )
            startActivityForResult(
                currencyIntent,
                REQUEST_QUOTE_COUNTRY
            )
        }

        swapButton.setOnClickListener {
            SharedPreferenceHelper.apply {
                baseCurrency = quoteCountry?.code
                quoteCurrency = baseCountry?.code
            }

            swapButton.performHapticContextClick()
            updateTable()
        }

        updateTable()
    }

    private fun updateTable() {
        baseCountry = CountryDataController.getCountryByCode(SharedPreferenceHelper.baseCurrency)
        quoteCountry = CountryDataController.getCountryByCode(SharedPreferenceHelper.quoteCurrency)

        viewModel.updateConversionRate(baseCountry?.rate, quoteCountry?.rate)
        viewModel.updateTable(rowList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_BASE_COUNTRY -> {
                val countryCode = data?.getStringExtra(Keys.Country.Code.name)
                SharedPreferenceHelper.baseCurrency = countryCode

                if (isExpanded) onRowCollapse()
                updateTable()
            }

            REQUEST_QUOTE_COUNTRY -> {
                val countryCode = data?.getStringExtra(Keys.Country.Code.name)
                SharedPreferenceHelper.quoteCurrency = countryCode

                if (isExpanded) onRowCollapse()
                updateTable()
            }
        }
    }

    private fun onRowExpand(selectedRow: Int) {
        currencyTableLayout.performHapticContextClick()
        rowList.forEachIndexed { index, tableRow ->
            if (index != selectedRow && index != (selectedRow + 1)) {
                tableRow.isVisible = false
            } else {
                tableRow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorAccent
                    )
                )
            }
        }

        viewModel.expandTable(
            activity = requireActivity(),
            tableLayout = currencyTableLayout,
            expandQuantifier = selectedRow,
            expandedList = expandedList,
            listener = this
        )
    }

    private fun onRowCollapse() {
        currencyTableLayout.performHapticContextClick()
        expandedList.forEach {
            currencyTableLayout.removeView(it)
        }

        rowList.forEach {
            it.isVisible = true
            it.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_cell_dark)
        }
    }

    /**
     * RowListener
     */
    override fun onSwipeLeft(multiplier: Double) {
        if (!isExpanded) {
            if (SharedPreferenceHelper.isPersistentMultiplierEnabled) {
                SharedPreferenceHelper.multiplier = multiplier.toInt()
            }

            currencyTableLayout.performHapticContextClick()
            viewModel.updateTable(rowList)
        }
    }

    override fun onSwipeRight(multiplier: Double) {
        if (!isExpanded) {
            if (SharedPreferenceHelper.isPersistentMultiplierEnabled) {
                SharedPreferenceHelper.multiplier = multiplier.toInt()
            }

            currencyTableLayout.performHapticContextClick()
            viewModel.updateTable(rowList)
        }
    }

    override fun onRowClicked(position: Int) {
        if (isExpanded) {
            onRowCollapse()
        } else {
            onRowExpand(position)
        }

        isExpanded = !isExpanded
    }
}