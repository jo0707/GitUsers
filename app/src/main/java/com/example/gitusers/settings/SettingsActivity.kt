package com.example.gitusers.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gitusers.R
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.main_toolbar.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setToolbar()
        setFragment()
    }

    private fun setFragment() =
        supportFragmentManager.beginTransaction().add(R.id.preferencesContainer, SettingsFragment()).commit()
    

    private fun setToolbar() {
        toolbarTitle.text   = getString(R.string.settings)
        
        (settingsToolbar as Toolbar).apply {
            setNavigationIcon(R.drawable.ic_back_white_24)
            setNavigationOnClickListener { finish() }
        }
    }

}