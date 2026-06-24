package com.cibertec.logivalgmml.repositories

import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun currentUid(): String? = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<String> = runCatching {
        require(email.isNotBlank()) { "Ingresa tu correo." }
        require(password.isNotBlank()) { "Ingresa tu contraseña." }

        val result = auth.signInWithEmailAndPassword(email.trim(), password).awaitTask()
        result.user?.uid ?: error("No se pudo iniciar sesión.")
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        require(email.trim().isNotBlank()) { "Ingresa tu correo electrónico." }
        require(email.trim().contains("@")) { "Ingresa un correo válido." }

        auth.sendPasswordResetEmail(email.trim()).awaitTask()
    }

    suspend fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        role: String
    ): Result<String> = runCatching {
        require(fullName.trim().length >= 3) { "Ingresa tu nombre completo." }
        require(phone.trim().length >= 7) { "Ingresa un teléfono válido." }
        require(email.contains("@")) { "Ingresa un correo válido." }
        require(password.length >= 6) { "La contraseña debe tener mínimo 6 caracteres." }

        val result = auth.createUserWithEmailAndPassword(email.trim(), password).awaitTask()
        val uid = result.user?.uid ?: error("No se pudo crear el usuario.")

        val user = AppUser(
            uid = uid,
            name = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            role = role,
            status = UserStatus.ACTIVO,
            createdAt = System.currentTimeMillis()
        )

        db.collection("users").document(uid).set(user, SetOptions.merge()).awaitTask()
        uid
    }

    suspend fun getCurrentUserProfile(): AppUser? {
        val uid = currentUid() ?: return null
        val document = db.collection("users").document(uid).get().awaitTask()
        return document.toObject(AppUser::class.java)
    }

    fun logout() {
        auth.signOut()
    }
}


// juan.perez@gmail.com -- contra: juanperez123 -> TRANSPORTISTA
// israel@gmail.com     -- contra: israel1410   -> PERSONAL DE CONTROL
// ana.torres@gmail.com -- contra: anatorres123 -> COMERCIANTE
// ramirols.dev@gmail.com -- contra: admin123 -> ADMINISTRADOR