package com.example.presentation.utils
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {
    fun applyLocale(context: Context, languageTag: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = if (languageTag.isEmpty())
                LocaleList.getEmptyLocaleList()
            else
                LocaleList.forLanguageTags(languageTag)
        } else {
            val locale = if (languageTag.isEmpty()) Locale.getDefault() else Locale(languageTag)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            (context as? android.app.Activity)?.recreate()
        }
    }
}