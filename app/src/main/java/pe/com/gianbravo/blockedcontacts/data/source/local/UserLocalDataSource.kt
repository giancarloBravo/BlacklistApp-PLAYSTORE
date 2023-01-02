package pe.com.gianbravo.blockedcontacts.data.source.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import pe.com.gianbravo.blockedcontacts.domain.User

/**
 * @author Giancarlo Bravo Anlas
 *
 */

class UserLocalDataSource(private val context: Context) {

    companion object {
        const val BLACKLIST_PREFERENCES = "blacklist_preferences"
        const val USER_SESSION = "user_session"
        const val FIRST_TIME = "FIRST_TIME"
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            BLACKLIST_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    val accessToken: String?
        get() = userSession?.accessToken

    val refreshToken: String?
        get() = userSession?.refreshToken

    var userSession: User?
        get() = Gson().fromJson(
            getSharedPreferences(context).getString(USER_SESSION, null),
            User::class.java
        )
        set(value) {
            val userAsJson = Gson().toJson(value)
            getSharedPreferences(context).edit(commit = true) {
                putString(USER_SESSION, userAsJson)
            }
        }

    var isFirstTime: Boolean?
        get() = Gson().fromJson(
            getSharedPreferences(context).getString(FIRST_TIME, null),
            Boolean::class.java
        )
        set(value: Boolean?) {
            val userAsJson = Gson().toJson(value)
            getSharedPreferences(context).edit(commit = true) {
                putString(FIRST_TIME, userAsJson)
            }
        }

    fun isUserLoggedIn(): Boolean {
        return userSession != null
        // TODO GBA IMPLEMENT TOKENS
        //return accessToken != null && refreshToken != null
    }

    fun clear() {
        userSession = null
    }
}