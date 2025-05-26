package com.abdl.mydicodingstories

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
// import androidx.appcompat.app.AppCompatDelegate // You might still need this for night mode
import com.abdl.mydicodingstories.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        applyPersistedLocale()
    }

    private fun applyPersistedLocale() {
        // sessionManager should be injected by the time onCreate is called for Application
        // if Hilt is set up correctly.
        if (::sessionManager.isInitialized) { // Check if injected
            val languageCode = sessionManager.fetchLanguage() // Will default to "in"
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            // Fallback if sessionManager isn't injected yet (shouldn't happen with Hilt in onCreate)
            // Or handle this scenario as an error/log
            val defaultConfig = resources.configuration
            val defaultLocale = Locale(SessionManager.DEFAULT_LANGUAGE_CODE) // Use static default
            if (defaultConfig.locale != defaultLocale) {
                Locale.setDefault(defaultLocale)
                defaultConfig.setLocale(defaultLocale)
                resources.updateConfiguration(defaultConfig, resources.displayMetrics)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Re-apply the persisted locale to ensure it overrides system changes if desired
        applyPersistedLocale()
    }
}