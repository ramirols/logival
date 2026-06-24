package com.cibertec.logivalgmml.repositories

import com.cibertec.logivalgmml.models.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}

class DashboardRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getSummary(): Result<DashboardSummary> = runCatching {
        val requests = db.collection("access_requests").get().awaitTask()
            .documents.mapNotNull { it.toObject(AccessRequest::class.java) }

        val incidents = db.collection("incidents").get().awaitTask()
            .documents.mapNotNull { it.toObject(Incident::class.java) }

        val logs = db.collection("access_logs").get().awaitTask()
            .documents.mapNotNull { it.toObject(AccessLog::class.java) }

        val finalized = requests.filter {
            it.status == RequestStatus.FINALIZADA && it.durationMinutes > 0
        }

        val avg = if (finalized.isNotEmpty()) {
            finalized.map { it.durationMinutes }.average().toLong()
        } else {
            0L
        }

        DashboardSummary(
            solicitudesPendientes = requests.count { it.status == RequestStatus.PENDIENTE },
            vehiculosDentro = requests.count { it.status == RequestStatus.EN_MERCADO },
            ingresosHoy = logs.count { sameDay(it.entryTime, System.currentTimeMillis()) },
            salidasHoy = logs.count { sameDay(it.exitTime, System.currentTimeMillis()) },
            incidenciasAbiertas = incidents.count {
                it.status == IncidentStatus.ABIERTA || it.status == IncidentStatus.EN_REVISION
            },
            permanenciaPromedio = avg
        )
    }

    private fun sameDay(timeA: Long, timeB: Long): Boolean {
        if (timeA <= 0L || timeB <= 0L) return false

        val a = Calendar.getInstance().apply { timeInMillis = timeA }
        val b = Calendar.getInstance().apply { timeInMillis = timeB }

        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

}
