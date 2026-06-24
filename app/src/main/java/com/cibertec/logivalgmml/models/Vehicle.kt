package com.cibertec.logivalgmml.models

data class Vehicle(
    val vehicleId: String = "",
    val plate: String = "",
    val type: String = "",
    val driverName: String = "",
    val driverDni: String = "",
    val ownerUid: String = "",
    val photoUrl: String = "",
    val status: String = VehicleStatus.ACTIVO,
    val createdAt: Long = 0L
)

object VehicleStatus {
    const val ACTIVO = "ACTIVO"
    const val INACTIVO = "INACTIVO"
    const val OBSERVADO = "OBSERVADO"
}

data class Incident(
    val incidentId: String = "",
    val requestId: String = "",
    val vehicleId: String = "",
    val userId: String = "",
    val type: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val status: String = IncidentStatus.ABIERTA,
    val createdAt: Long = 0L
)

object IncidentStatus {
    const val ABIERTA = "ABIERTA"
    const val EN_REVISION = "EN_REVISION"
    const val RESUELTA = "RESUELTA"
    const val DESCARTADA = "DESCARTADA"
}
