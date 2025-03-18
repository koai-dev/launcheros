package com.twt.launcheros.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.twt.launcheros.R

class SettingsScreen : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
