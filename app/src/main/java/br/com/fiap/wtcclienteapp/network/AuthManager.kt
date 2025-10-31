package br.com.fiap.wtcclienteapp.network

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_USER_CPF = "user_cpf"
    
    private lateinit var prefs: SharedPreferences
    
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return if (::prefs.isInitialized) {
            prefs.getString(KEY_TOKEN, null)
        } else {
            null
        }
    }
    
    fun saveUserInfo(userId: Long?, userName: String?, userType: String?, userCpf: String?) {
        prefs.edit().apply {
            userId?.let { putLong(KEY_USER_ID, it) }
            userName?.let { putString(KEY_USER_NAME, it) }
            userType?.let { putString(KEY_USER_TYPE, it) }
            userCpf?.let { putString(KEY_USER_CPF, it) }
            apply()
        }
    }
    
    fun getUserId(): Long? {
        return if (::prefs.isInitialized && prefs.contains(KEY_USER_ID)) {
            prefs.getLong(KEY_USER_ID, -1).takeIf { it != -1L }
        } else {
            null
        }
    }
    
    fun getUserName(): String? {
        return if (::prefs.isInitialized) {
            prefs.getString(KEY_USER_NAME, null)
        } else {
            null
        }
    }
    
    fun getUserType(): String? {
        return if (::prefs.isInitialized) {
            prefs.getString(KEY_USER_TYPE, null)
        } else {
            null
        }
    }
    
    fun getUserCpf(): String? {
        return if (::prefs.isInitialized) {
            prefs.getString(KEY_USER_CPF, null)
        } else {
            null
        }
    }
    
    fun clearAuth() {
        prefs.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
