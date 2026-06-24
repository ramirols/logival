package com.cibertec.logivalgmml.screens.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VehicleFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { VehicleRepository() }

    var plate by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var driverDni by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LogiValScaffold(
        title = "Nuevo vehículo",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipo de vehículo") },
                placeholder = { Text("Camión, furgón, mototaxi...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = driverName,
                onValueChange = { driverName = it },
                label = { Text("Nombre del conductor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = driverDni,
                onValueChange = { driverDni = it },
                label = { Text("DNI del conductor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = {
                    loading = true
                    message = ""

                    scope.launch {
                        val uid = authRepository.currentUid().orEmpty()

                        repository.createVehicle(
                            plate = plate,
                            type = type,
                            driverName = driverName,
                            driverDni = driverDni,
                            ownerUid = uid
                        ).onSuccess {
                            navController.popBackStack()
                        }.onFailure {
                            message = it.message ?: "No se pudo registrar."
                        }

                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogiValGreen),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (loading) "Guardando..." else "Guardar vehículo")
            }

            if (message.isNotBlank()) {
                Text(message, color = LogiValRed)
            }
        }
    }
}