package cstjean.mobile.tp_fedex

import android.content.Context

/**
 * Clé permettant du fichier qui sauvegarde les préférences
 */
private const val PREFERENCE_FILE_KEY = "cstjean.mobile.tp_fedex.PREFERENCE_FILE_KEY"

/**
 * Clé pour la préférence de recherche
 */
private const val PREF_SEARCH_QUERY = "searchQuery"

/**
 * Object QueryPreferences
 *
 * @author Sébastien Fortier
 * @author Ethan Leduc
 */
object QueryPreferences {

    /**
     * Permet d'obtenir la préférence de recherche
     *
     * @param context Le contexte de l'application
     *
     * @return La préférence de recherche
     */
    fun getStoredQuery(context: Context): String {
        val prefs = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
        return prefs.getString(PREF_SEARCH_QUERY, "")!!
    }

    /**
     * Permet d'ètablir la préférence de recherche
     *
     * @param context Le contexte de l'application
     * @param query La nouvelle préférence
     *
     * @return La nouvelle préférence
     */
    fun setStoredQuery(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE) ?: return
        with (prefs.edit()) {
            putString(PREF_SEARCH_QUERY, query)
            apply()
        }
    }
}