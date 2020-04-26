package com.delacrixmorgan.squark.ui.preference

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.Keys
import com.delacrixmorgan.squark.ui.preference.country.CountryListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_preference_navigation.*
import kotlinx.serialization.UnstableDefault

@UnstableDefault
class PreferenceNavigationActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        fun create(context: Context, countryCode: String? = null): Intent {
            return Intent(context, PreferenceNavigationActivity::class.java).apply {
                putExtra(Keys.Country.Code.name, countryCode)
            }
        }
    }

    private var countryCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_navigation)

        if (intent.extras != null) {
            countryCode = intent.getStringExtra(Keys.Country.Code.name)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.itemCountries
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val existingFragment = supportFragmentManager.findFragmentById(this.contentContainer.id)
        val targetFragment: Fragment = when (menuItem.itemId) {
            R.id.itemCountries -> CountryListFragment.create(this.countryCode)
            R.id.itemSupport -> SupportListFragment.create()
            R.id.itemSettings -> SettingsListFragment.create()
            else -> CountryListFragment.create()
        }

        if (existingFragment != null && existingFragment::class.java == targetFragment::class.java) {
            return true
        }

        supportFragmentManager.commit(allowStateLoss = true) {
            replace(contentContainer.id, targetFragment)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}