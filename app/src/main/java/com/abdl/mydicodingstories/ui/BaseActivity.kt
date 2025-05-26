package com.abdl.mydicodingstories.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abdl.mydicodingstories.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun attachBaseContext(newBase: Context) {
        val tempSessionManager = SessionManager(newBase) // For early access
        val languageCode = tempSessionManager.fetchLanguage() // This will now default to "in"
        val localeToSet = Locale(languageCode)
        super.attachBaseContext(updateBaseContextLocale(newBase, localeToSet))
    }

    private fun updateBaseContextLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No explicit call to applySelectedAppLocale needed here for initial setup
        // as attachBaseContext should handle it.
    }

    protected fun setLocaleAndRecreate(languageCode: String) {
        sessionManager.saveLanguage(languageCode)
        // The rest of this method remains the same, BaseActivity will be recreated,
        // and attachBaseContext will pick up the newly saved language.
        recreate()
    }

    // This function can be simplified or removed if BaseActivity's attachBaseContext
    // and MyApplication's onConfigurationChanged handle all cases.
    // For now, let's assume BaseActivity handles its own context correctly.
    /*
    fun applySelectedAppLocale(context: Context) {
        val languageCode = sessionManager.fetchLanguage()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
    */
}