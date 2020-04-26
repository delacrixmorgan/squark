package com.delacrixmorgan.squark.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.delacrixmorgan.squark.R
import com.delacrixmorgan.squark.common.launchPlayStore
import com.delacrixmorgan.squark.common.performHapticContextClick
import kotlinx.android.synthetic.main.fragment_support_list.*

class SupportListFragment : Fragment() {
    companion object {
        private const val KINGS_CUP_PACKAGE_NAME = "com.delacrixmorgan.kingscup"
        private const val MAMIKA_PACKAGE_NAME = "com.delacrixmorgan.mamika"

        fun create() = SupportListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val packageName = view.context.packageName

        starImageView.setOnClickListener {
            starImageView.performHapticContextClick()
            personImageView.setImageResource(R.drawable.ic_human_happy)
            starImageView.setColorFilter(ContextCompat.getColor(view.context, R.color.colorAccent))

            launchPlayStore(packageName)
        }

        rateButton.setOnClickListener {
            rateButton.performHapticContextClick()
            personImageView.setImageResource(R.drawable.ic_human_happy)
            launchPlayStore(packageName)
        }

        kingscupViewGroup.setOnClickListener {
            kingscupViewGroup.performHapticContextClick()
            launchPlayStore(KINGS_CUP_PACKAGE_NAME)
        }

        mamikaViewGroup.setOnClickListener {
            kingscupViewGroup.performHapticContextClick()
            launchPlayStore(MAMIKA_PACKAGE_NAME)
        }
    }
}