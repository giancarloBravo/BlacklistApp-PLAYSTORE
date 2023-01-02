package pe.com.gianbravo.blockedcontacts.domain

import androidx.annotation.Keep

/**
 * @author Giancarlo Bravo Anlas
 *
 */
data class BlacklistContacts(val count: Int, var list: ArrayList<String>)