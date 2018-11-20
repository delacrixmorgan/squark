package com.delacrixmorgan.squark.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.delacrixmorgan.squark.common.launchWebsite
import com.delacrixmorgan.squark.common.shareAppIntent
import com.delacrixmorgan.squark.databinding.FragmentSettingsListBinding
import kotlinx.android.synthetic.main.fragment_settings_list.*

/**
 * SettingsListFragment
 * squark-android
 *
 * Created by Delacrix Morgan on 17/11/2018.
 * Copyright (c) 2018 licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.
 */

class SettingsListFragment : Fragment() {
    companion object {
        private const val SOURCE_CODE_URL = "https://github.com/delacrixmorgan/squark-android"

        fun newInstance() = SettingsListFragment()
    }

    private lateinit var dataBinding: FragmentSettingsListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.dataBinding = FragmentSettingsListBinding.inflate(inflater, container, false)
        return this.dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
    }

    private fun setupListeners() {
        val context = this.context ?: return

        this.creditsViewGroup.setOnClickListener {

        }

        this.shareViewGroup.setOnClickListener {
            context.shareAppIntent()
        }

        this.sourceCodeViewGroup.setOnClickListener {
            context.launchWebsite(SOURCE_CODE_URL)
        }
    }
}