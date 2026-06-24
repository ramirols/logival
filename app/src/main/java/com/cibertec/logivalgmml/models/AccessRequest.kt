package com.cibertec.logivalgmml.models

data class AccessRequest(
    val requestId: String = "",
    val userId: String = "",
    val vehicleId: String = "",
    val vehiclePlate: String = "",
    val pavilionId: String = "",
    val reason: String = "",
    val productType: String = "",
    val estimatedWeight: String = "",
    val status: String = RequestStatus.PENDIENTE,
    val qrCode: String = "",
    val requestedAt: Long = 0L,
    val approvedAt: Long = 0L,
    val entryTime: Long = 0L,
    val exitTime: Long = 0L,
    val durationMinutes: Long = 0L,
    val calculatedFee: Double = 0.0,
    val rejectedReason: String = ""
)



data class AccessLog(
    val logId: String = "",
    val requestId: String = "",
    val vehicleId: String = "",
    val controlUid: String = "",
    val entryTime: Long = 0L,
    val exitTime: Long = 0L,
    val durationMinutes: Long = 0L,
    val calculatedFee: Double = 0.0,
    val status: String = ""
)



object RequestStatus {
    const val PENDIENTE = "PENDIENTE"
    const val APROBADA = "APROBADA"
    const val RECHAZADA = "RECHAZADA"
    const val EN_MERCADO = "EN_MERCADO"
    const val FINALIZADA = "FINALIZADA"
}
