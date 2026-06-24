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

fun String.normalizedRole(): String {
    return this.trim().uppercase().replace(" ", "_")
}

fun String.isAdminRole(): Boolean {
    return this.normalizedRole() == UserRole.ADMIN
}

fun String.isControlRole(): Boolean {
    val role = this.normalizedRole()

    return role == UserRole.CONTROL ||
            role == UserRole.ADMIN
}

fun String.roleLabel(): String {
    return when (this.normalizedRole()) {
        UserRole.TRANSPORTISTA -> "Transportista"
        UserRole.COMERCIANTE -> "Comerciante"
        UserRole.CONTROL -> "Personal de control"
        UserRole.ADMIN -> "Administrador"
        else -> this
    }
}