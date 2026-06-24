package com.cibertec.logivalgmml.repositories

import com.cibertec.logivalgmml.models.AccessLog
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.models.isControlRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.max

class RequestRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createRequest(
        uid: String,
        vehicle: Vehicle,
        pavilionId: String,
        reason: String,
        productType: String,
        estimatedWeight: String,
        merchantUid: String = "",
        merchantName: String = ""
    ): Result<String> = runCatching {
        require(uid.isNotBlank()) { "Sesión no válida." }
        require(vehicle.vehicleId.isNotBlank()) { "Selecciona un vehículo." }
        require(pavilionId.isNotBlank()) { "Ingresa el pabellón o zona." }
        require(reason.isNotBlank()) { "Ingresa el motivo de ingreso." }

        val ref = db.collection("access_requests").document()

        val request = AccessRequest(
            requestId = ref.id,
            userId = uid,
            merchantUid = merchantUid,
            merchantName = merchantName,
            vehicleId = vehicle.vehicleId,
            vehiclePlate = vehicle.plate,
            pavilionId = pavilionId.trim(),
            reason = reason.trim(),
            productType = productType.trim(),
            estimatedWeight = estimatedWeight.trim(),
            status = RequestStatus.PENDIENTE,
            qrCode = ref.id,
            requestedAt = System.currentTimeMillis()
        )

        ref.set(request, SetOptions.merge()).awaitTask()
        ref.id
    }

    suspend fun getRequests(uid: String, role: String): Result<List<AccessRequest>> = runCatching {
        val normalizedRole = role.trim().uppercase().replace(" ", "_")

        if (
            normalizedRole == "CONTROL" ||
            normalizedRole == "ADMIN" ||
            normalizedRole == "ADMINISTRADOR"
        ) {
            val snapshot = db.collection("access_requests")
                .get()
                .awaitTask()

            return@runCatching snapshot.documents
                .mapNotNull { it.toObject(AccessRequest::class.java) }
                .sortedByDescending { it.requestedAt }
        }

        if (normalizedRole == "COMERCIANTE") {
            val ownSnapshot = db.collection("access_requests")
                .whereEqualTo("userId", uid)
                .get()
                .awaitTask()

            val associatedSnapshot = db.collection("access_requests")
                .whereEqualTo("merchantUid", uid)
                .get()
                .awaitTask()

            val ownRequests = ownSnapshot.documents
                .mapNotNull { it.toObject(AccessRequest::class.java) }

            val associatedRequests = associatedSnapshot.documents
                .mapNotNull { it.toObject(AccessRequest::class.java) }

            return@runCatching (ownRequests + associatedRequests)
                .distinctBy { it.requestId }
                .sortedByDescending { it.requestedAt }
        }

        val snapshot = db.collection("access_requests")
            .whereEqualTo("userId", uid)
            .get()
            .awaitTask()

        snapshot.documents
            .mapNotNull { it.toObject(AccessRequest::class.java) }
            .sortedByDescending { it.requestedAt }
    }

    suspend fun getById(requestId: String): AccessRequest? {
        if (requestId.isBlank()) return null
        val document = db.collection("access_requests").document(requestId).get().awaitTask()
        return document.toObject(AccessRequest::class.java)
    }

    suspend fun approve(requestId: String): Result<Unit> = runCatching {
        require(requestId.isNotBlank()) { "Solicitud inválida." }

        db.collection("access_requests").document(requestId)
            .update(
                mapOf(
                    "status" to RequestStatus.APROBADA,
                    "approvedAt" to System.currentTimeMillis(),
                    "qrCode" to requestId
                )
            ).awaitTask()
    }

    suspend fun reject(requestId: String): Result<Unit> = runCatching {
        require(requestId.isNotBlank()) { "Solicitud inválida." }

        db.collection("access_requests").document(requestId)
            .update(
                mapOf(
                    "status" to RequestStatus.RECHAZADA,
                    "rejectedReason" to "Solicitud rechazada por control"
                )
            ).awaitTask()
    }

    suspend fun validateQr(requestId: String, controlUid: String): Result<String> = runCatching {
        require(requestId.isNotBlank()) { "Ingresa un código QR válido." }

        val ref = db.collection("access_requests").document(requestId)
        val request = ref.get().awaitTask().toObject(AccessRequest::class.java)
            ?: error("No existe una solicitud con ese QR.")

        val now = System.currentTimeMillis()

        when (request.status) {
            RequestStatus.APROBADA -> {
                ref.update(
                    mapOf(
                        "status" to RequestStatus.EN_MERCADO,
                        "entryTime" to now
                    )
                ).awaitTask()

                saveLog(
                    AccessLog(
                        requestId = request.requestId,
                        vehicleId = request.vehicleId,
                        controlUid = controlUid,
                        entryTime = now,
                        status = RequestStatus.EN_MERCADO
                    )
                )

                "Ingreso registrado. Vehículo ${request.vehiclePlate} ahora está EN MERCADO."
            }

            RequestStatus.EN_MERCADO -> {
                val duration = max(1L, (now - request.entryTime) / 60000L)
                val fee = calculateSimulatedFee(duration)

                ref.update(
                    mapOf(
                        "status" to RequestStatus.FINALIZADA,
                        "exitTime" to now,
                        "durationMinutes" to duration,
                        "calculatedFee" to fee
                    )
                ).awaitTask()

                saveLog(
                    AccessLog(
                        requestId = request.requestId,
                        vehicleId = request.vehicleId,
                        controlUid = controlUid,
                        entryTime = request.entryTime,
                        exitTime = now,
                        durationMinutes = duration,
                        calculatedFee = fee,
                        status = RequestStatus.FINALIZADA
                    )
                )

                "Salida registrada. Permanencia: $duration min. Tarifa simulada: S/ ${"%.2f".format(fee)}."
            }

            RequestStatus.PENDIENTE -> error("La solicitud todavía está pendiente.")
            RequestStatus.RECHAZADA -> error("La solicitud fue rechazada.")
            RequestStatus.FINALIZADA -> error("Esta solicitud ya fue finalizada.")
            else -> error("Estado no válido: ${request.status}")
        }
    }

    private suspend fun saveLog(log: AccessLog) {
        val ref = db.collection("access_logs").document()
        ref.set(log.copy(logId = ref.id), SetOptions.merge()).awaitTask()
    }

    private fun calculateSimulatedFee(durationMinutes: Long): Double {
        return when {
            durationMinutes <= 30 -> 0.0
            durationMinutes <= 60 -> 2.0
            else -> 5.0
        }
    }

}