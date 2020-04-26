package com.delacrixmorgan.squark.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.BuildConfig
import androidx.fragment.app.Fragment
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.SharedPreferenceHelper
import com.delacrixmorgan.squark.common.launchWebsite
import com.delacrixmorgan.squark.common.shareAppIntent
import com.delacrixmorgan.squark.databinding.FragmentSettingsListBinding
import com.delacrixmorgan.squark.ui.preference.credit.CreditActivity
import kotlinx.android.synthetic.main.cell_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings_list.*

class SettingsListFragment : Fragment() {

    companion object {
        private const val SOURCE_CODE_URL = "https://github.com/delacrixmorgan/squark-android"

        fun create() = SettingsListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSettingsListBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildNumberTextView.text = getString(
            R.string.message_build_version_name,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        multiplierViewGroup.switchCompat.isChecked =
            SharedPreferenceHelper.isPersistentMultiplierEnabled

        multiplierViewGroup.setOnClickListener {
            val updatedSwitchCheck = !this.multiplierViewGroup.switchCompat.isChecked
            multiplierViewGroup.switchCompat.isChecked = updatedSwitchCheck

            SharedPreferenceHelper.apply {
                multiplier = 1
                isPersistentMultiplierEnabled = updatedSwitchCheck
            }
        }

        creditsViewGroup.setOnClickListener {
            val launchIntent = CreditActivity.create(view.context)
            startActivity(launchIntent)
        }

        shareViewGroup.setOnClickListener {
            val shareMessage = getString(R.string.fragment_settings_list_share_message)
            view.context.shareAppIntent(shareMessage)
        }

        sourceCodeViewGroup.setOnClickListener {
            launchWebsite(SOURCE_CODE_URL)
        }
    }
}