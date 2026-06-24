package com.cibertec.logivalgmml.screens.incidents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.IncidentRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun IncidentFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { IncidentRepository() }

    var requestId by remember { mutableStateOf("") }
    var vehicleId by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LogiValScaffold(
        title = "Nueva incidencia",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipo de incidencia") },
                placeholder = { Text("Daño, demora, congestión...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = requestId,
                onValueChange = { requestId = it },
                label = { Text("Solicitud ID opcional") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = vehicleId,
                onValueChange = { vehicleId = it },
                label = { Text("Vehículo ID opcional") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("URL de evidencia opcional") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = {
                    loading = true
                    message = ""

                    scope.launch {
                        val uid = authRepository.currentUid().orEmpty()

                        repository.createIncident(
                            uid = uid,
                            requestId = requestId,
                            vehicleId = vehicleId,
                            type = type,
                            description = description,
                            photoUrl = photoUrl
                        ).onSuccess {
                            navController.popBackStack()
                        }.onFailure {
                            message = it.message ?: "No se pudo registrar la incidencia."
                        }

                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogiValGreen),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (loading) "Guardando..." else "Registrar incidencia")
            }

            if (message.isNotBlank()) {
                Text(message, color = LogiValRed)
            }
        }
    }
}