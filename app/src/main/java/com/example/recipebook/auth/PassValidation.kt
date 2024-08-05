package com.example.recipebook.auth

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.recipebook.R

class PassValidation(val context: Context) {

    fun isValidPass(password: String): String? {
        if (password.length < 8) {
            return getString(context, R.string.err_pass_lenght)
        }
        if (!password.matches(Regex(".*[A-Z].*"))) {
            return getString(context, R.string.err_pass_uppercase)
        }
        if (!password.matches(Regex(".*[a-z].*"))) {
            return getString(context, R.string.err_pass_lowercase)
        }
        if (!password.matches(Regex(".*[0-9].*"))) {
            return getString(context, R.string.err_pass_digit)
        }
        return null
    }
}