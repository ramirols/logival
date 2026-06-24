package com.cibertec.logivalgmml.repositories

import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.models.VehicleStatus
import com.cibertec.logivalgmml.models.isControlRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class VehicleRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createVehicle(
        plate: String,
        type: String,
        driverName: String,
        driverDni: String,
        ownerUid: String
    ): Result<String> = runCatching {
        require(ownerUid.isNotBlank()) { "Sesión no válida." }
        require(plate.trim().length >= 5) { "Ingresa una placa válida." }
        require(type.isNotBlank()) { "Ingresa el tipo de vehículo." }
        require(driverName.isNotBlank()) { "Ingresa el nombre del conductor." }

        val ref = db.collection("vehicles").document()

        val vehicle = Vehicle(
            vehicleId = ref.id,
            plate = plate.trim().uppercase(),
            type = type.trim(),
            driverName = driverName.trim(),
            driverDni = driverDni.trim(),
            ownerUid = ownerUid,
            status = VehicleStatus.ACTIVO,
            createdAt = System.currentTimeMillis()
        )

        ref.set(vehicle, SetOptions.merge()).awaitTask()
        ref.id
    }

    suspend fun getVehicles(uid: String, role: String): Result<List<Vehicle>> = runCatching {
        val snapshot = if (role.isControlRole()) {
            db.collection("vehicles").get().awaitTask()
        } else {
            db.collection("vehicles").whereEqualTo("ownerUid", uid).get().awaitTask()
        }

        snapshot.documents
            .mapNotNull { it.toObject(Vehicle::class.java) }
            .sortedByDescending { it.createdAt }
    }

    suspend fun getVehicleById(vehicleId: String): Vehicle? {
        if (vehicleId.isBlank()) return null
        val doc = db.collection("vehicles").document(vehicleId).get().awaitTask()
        return doc.toObject(Vehicle::class.java)
    }

}