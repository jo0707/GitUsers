package com.example.gitusers.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.gitusers.R
import com.example.gitusers.reminder.AlarmReceiver

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var languageKey: String
    private lateinit var reminderKey: String
    private lateinit var languagePreference: Preference
    private lateinit var reminderSwitch: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        alarmReceiver   = AlarmReceiver()

        init()
        setSharedPreferences()
    }
    
    private fun init() {
        languageKey         = getString(R.string.key_language_preference)
        reminderKey         = getString(R.string.key_reminder_switch)

        languagePreference  = findPreference<Preference>(languageKey) as Preference
        reminderSwitch      = findPreference<SwitchPreference>(reminderKey) as SwitchPreference

        languagePreference.setOnPreferenceClickListener {
            val languageIntent  = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(languageIntent)
            return@setOnPreferenceClickListener true
        }
    }

    private fun setSharedPreferences() {
        val sp  = preferenceManager.sharedPreferences
        reminderSwitch.isChecked    = sp.getBoolean(reminderKey, true)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences?.getBoolean(reminderKey, true) as Boolean) {
            alarmReceiver.setRepeatingAlarm(context as Context, AlarmReceiver.TYPE_REPEATING, getString(R.string.reminder_content))
            reminderSwitch.isChecked    = true
        } else {
            alarmReceiver.cancelAlarm(context as Context, AlarmReceiver.TYPE_REPEATING)
            reminderSwitch.isChecked    = false
        }
    }
    
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}