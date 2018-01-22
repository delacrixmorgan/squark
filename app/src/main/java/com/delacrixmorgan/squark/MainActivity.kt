package com.delacrixmorgan.squark

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.delacrixmorgan.squark.common.changeAppOverview
import com.delacrixmorgan.squark.common.showFragment

/**
 * Created by Delacrix Morgan on 03/07/2017.
 **/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeAppOverview(this, theme)
        showFragment(this, LaunchFragment.newInstance())
    }
}
