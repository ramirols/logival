package com.cibertec.logivalgmml.models

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = UserRole.TRANSPORTISTA,
    val status: String = UserStatus.ACTIVO,
    val createdAt: Long = 0L
)



fun String.isControlRole(): Boolean {
    return this == UserRole.CONTROL || this == UserRole.ADMIN
}

fun String.roleLabel(): String {
    return when (this) {
        UserRole.TRANSPORTISTA -> "Transportista"
        UserRole.COMERCIANTE -> "Comerciante"
        UserRole.CONTROL -> "Personal de control"
        UserRole.ADMIN -> "Administrador"
        else -> this
    }
}



