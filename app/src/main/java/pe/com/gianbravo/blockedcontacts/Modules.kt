package pe.com.gianbravo.blockedcontacts

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pe.com.gianbravo.blockedcontacts.data.source.UserRepository
import pe.com.gianbravo.blockedcontacts.data.source.local.UserLocalDataSource

/**
 * @author Giancarlo Bravo Anlas
 *
 */

val dataModule = module {
    single { UserLocalDataSource(androidContext()) }
    single { UserRepository.UserRepositoryImpl(get()) as UserRepository }
}