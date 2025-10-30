package br.com.fiap.wtcclienteapp

import android.content.Context

object NotesStorage {
    private const val PREF = "notes_pref"

    fun get(context: Context, clientId: String): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(clientId, "") ?: ""
    }

    fun put(context: Context, clientId: String, note: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(clientId, note)
            .apply()
    }
}


