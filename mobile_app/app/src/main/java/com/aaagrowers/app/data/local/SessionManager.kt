package com.aaagrowers.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.aaagrowers.app.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("aaa_growers_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAuth(token: String, user: User) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_json", gson.toJson(user))
            .putString("user_role", user.role)
            .apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun getUser(): User? {
        val json = prefs.getString("user_json", null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserRole(): String? = prefs.getString("user_role", null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun getServerUrl(): String {
        return prefs.getString("server_url", "http://10.0.2.2:5000/api/") ?: "http://10.0.2.2:5000/api/"
    }

    fun setServerUrl(url: String) {
        var cleanUrl = url.trim()
        if (!cleanUrl.endsWith("/")) cleanUrl += "/"
        if (!cleanUrl.endsWith("api/")) cleanUrl += "api/"
        prefs.edit().putString("server_url", cleanUrl).apply()
    }

    fun logout() {
        val savedServerUrl = getServerUrl()
        prefs.edit().clear().putString("server_url", savedServerUrl).apply()
    }
}
