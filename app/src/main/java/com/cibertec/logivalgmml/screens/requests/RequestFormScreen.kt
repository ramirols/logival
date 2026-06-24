package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RequestFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val vehicleRepository = remember { VehicleRepository() }
    val requestRepository = remember { RequestRepository() }

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }

    var pavilion by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var estimatedWeight by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val user = authRepository.getCurrentUserProfile()
        val uid = authRepository.currentUid().orEmpty()

        vehicleRepository.getVehicles(uid, user?.role.orEmpty())
            .onSuccess {
                vehicles = it
                selectedVehicle = it.firstOrNull()
            }
    }

    LogiValScaffold(
        title = "Nueva solicitud",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Selecciona vehículo", color = LogiValGreenDark)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vehicles) { vehicle ->
                    FilterChip(
                        selected = selectedVehicle?.vehicleId == vehicle.vehicleId,
                        onClick = { selectedVehicle = vehicle },
                        label = { Text(vehicle.plate) }
                    )
                }
            }

            OutlinedTextField(
                value = pavilion,
                onValueChange = { pavilion = it },
                label = { Text("Pabellón o zona") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motivo de ingreso") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = productType,
                onValueChange = { productType = it },
                label = { Text("Tipo de producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = estimatedWeight,
                onValueChange = { estimatedWeight = it },
                label = { Text("Peso estimado") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = {
                    loading = true
                    message = ""

                    scope.launch {
                        val uid = authRepository.currentUid().orEmpty()
                        val vehicle = selectedVehicle

                        if (vehicle == null) {
                            message = "Primero registra o selecciona un vehículo."
                            loading = false
                            return@launch
                        }

                        requestRepository.createRequest(
                            uid = uid,
                            vehicle = vehicle,
                            pavilionId = pavilion,
                            reason = reason,
                            productType = productType,
                            estimatedWeight = estimatedWeight
                        ).onSuccess {
                            navController.popBackStack()
                        }.onFailure {
                            message = it.message ?: "No se pudo crear la solicitud."
                        }

                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogiValGreen),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (loading) "Enviando..." else "Enviar solicitud")
            }

            if (message.isNotBlank()) {
                Text(message, color = LogiValRed)
            }
        }
    }
}