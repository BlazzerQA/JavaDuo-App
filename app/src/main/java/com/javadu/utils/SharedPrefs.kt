package com.javadu.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "javaduo_prefs"
        private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
        private const val KEY_TODAY_XP = "today_xp"
    }

    var isOnboardingShown: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_SHOWN, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_SHOWN, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var dailyGoal: Int
        get() = prefs.getInt(KEY_DAILY_GOAL, 50)
        set(value) = prefs.edit().putInt(KEY_DAILY_GOAL, value).apply()

    fun getTodayXp(): Int {
        val today = getTodayDate()
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, "")
        return if (lastDate == today) {
            prefs.getInt(KEY_TODAY_XP, 0)
        } else {
            prefs.edit().putString(KEY_LAST_ACTIVE_DATE, today).putInt(KEY_TODAY_XP, 0).apply()
            0
        }
    }

    fun addTodayXp(xp: Int) {
        val current = getTodayXp()
        prefs.edit().putInt(KEY_TODAY_XP, current + xp).apply()
    }

    fun resetTodayXp() {
        prefs.edit().putInt(KEY_TODAY_XP, 0).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }

    private fun getTodayDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
