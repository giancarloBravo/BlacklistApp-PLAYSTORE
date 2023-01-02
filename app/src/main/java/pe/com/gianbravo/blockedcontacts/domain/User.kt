package pe.com.gianbravo.blockedcontacts.domain

/**
 * @author Giancarlo Bravo Anlas
 *
 */

data class User(
    val id: Int,
    var identifier: String? = null,
    var photo: String? = null,

    // Session
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var firebaseToken: String? = null,

    // Personal Information
    var names: String? = null,
    var lastNames: String? = null,
    var document: String? = null,
    var email: String? = null,
    var birthday: String? = null,
    var gender: String? = null,
    var cellphone: String? = null,
    // Status Information
    var state: Int? = -1,
    var firebaseDocument: String? = null
) {
    companion object {
        const val USER_REGISTRATION_INCOMPLETE = 0
        const val USER_REGISTERED = 1
        const val USER_NOT_REGISTERED = 2
        const val USER_PENDING_APPROVAL = 3

//        fun fromSignInData(data: SignInData): User {
//            val id =
//                if (data.userId == null) -1
//                else data.userId
//            return User(id).apply {
//                //businessName = signInData.businessName
//                accessToken = data.accessToken
//                refreshToken = data.refreshToken
//                firebaseToken = data.firebaseToken
//                document = data.document
//                email = data.email
//                birthday = data.birthday
//                gender = data.gender
//                cellphone = data.cellphone
//                state= data.state
//                identifier = data.identifier
//                firebaseDocument = data.firebaseDocument
//                names = data.name
//            }
//        }

        fun mock(): User {
            return User(20).apply {
                accessToken = "kj5l15j1l2kjlkfjlkjljlkjl"
                refreshToken = "tjltjlj5k1jl1j512jl12j5l12k5"
                firebaseToken = "mjltjlj5k1jl1j512jl12j5l12k5"
                names = "Gian"
                lastNames = "Bravo"
                document = "47723301"
                email = "dasxz@gmail.com"
                birthday = "10/10/20"
                gender = "M"
                state = USER_REGISTERED
                identifier = "d26cfb60-9aac-11ea-9821-853f5a899e86"
                firebaseDocument = "PN-24_06_2020"
            }
        }
    }
}