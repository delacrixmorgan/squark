package com.delacrixmorgan.squark.ui.preference.credit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.delacrixmorgan.squark.R

class CreditActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context) = Intent(context, CreditActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit)

        val fragment = CreditListFragment.create()
        supportFragmentManager.commit {
            replace(R.id.contentContainer, fragment)
        }
    }
}