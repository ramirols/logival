package com.cibertec.logivalgmml.repositories

import com.cibertec.logivalgmml.models.Incident
import com.cibertec.logivalgmml.models.IncidentStatus
import com.cibertec.logivalgmml.models.isControlRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class IncidentRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createIncident(
        uid: String,
        requestId: String,
        vehicleId: String,
        type: String,
        description: String,
        photoUrl: String = ""
    ): Result<String> = runCatching {
        require(uid.isNotBlank()) { "Sesión no válida." }
        require(type.isNotBlank()) { "Ingresa el tipo de incidencia." }
        require(description.trim().length >= 8) { "Describe mejor la incidencia." }

        val ref = db.collection("incidents").document()

        val incident = Incident(
            incidentId = ref.id,
            requestId = requestId.trim(),
            vehicleId = vehicleId.trim(),
            userId = uid,
            type = type.trim(),
            description = description.trim(),
            photoUrl = photoUrl.trim(),
            status = IncidentStatus.ABIERTA,
            createdAt = System.currentTimeMillis()
        )

        ref.set(incident, SetOptions.merge()).awaitTask()
        ref.id
    }

    suspend fun getIncidents(uid: String, role: String): Result<List<Incident>> = runCatching {
        val snapshot = if (role.isControlRole()) {
            db.collection("incidents").get().awaitTask()
        } else {
            db.collection("incidents").whereEqualTo("userId", uid).get().awaitTask()
        }

        snapshot.documents
            .mapNotNull { it.toObject(Incident::class.java) }
            .sortedByDescending { it.createdAt }
    }

}
