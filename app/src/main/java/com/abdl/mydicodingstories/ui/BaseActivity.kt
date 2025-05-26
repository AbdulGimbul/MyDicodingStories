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
        val tempSessionManager = SessionManager(newBase)
        val languageCode = tempSessionManager.fetchLanguage()
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
    }

    protected fun setLocaleAndRecreate(languageCode: String) {
        sessionManager.saveLanguage(languageCode)
        recreate()
    }
}