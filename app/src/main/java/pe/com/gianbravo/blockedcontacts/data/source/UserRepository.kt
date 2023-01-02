package pe.com.gianbravo.blockedcontacts.data.source

import pe.com.gianbravo.blockedcontacts.domain.User
import pe.com.gianbravo.blockedcontacts.data.source.local.UserLocalDataSource

/**
 * @author Giancarlo Bravo Anlas
 *
 */

interface UserRepository {

    val accessToken: String?
    val refreshToken: String?
    var userSession: User?
    var isFirstTime: Boolean?
    
    fun isUserLoggedIn(): Boolean
    fun clear()

    class UserRepositoryImpl
    constructor(
        private val localSource: UserLocalDataSource
    ) : UserRepository {

        override val accessToken: String?
            get() = localSource.accessToken

        override val refreshToken: String?
            get() = localSource.refreshToken

        override var userSession: User?
            get() = localSource.userSession
            set(value) {
                localSource.userSession = value
            }

        override var isFirstTime: Boolean?
            get() = localSource.isFirstTime
            set(value) {
                localSource.isFirstTime = value
            }

        override fun isUserLoggedIn(): Boolean {
            return localSource.isUserLoggedIn()
        }

        override fun clear() {
            localSource.clear()
        }
    }
}