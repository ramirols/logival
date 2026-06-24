package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.models.isControlRole
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.StatusChip
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RequestDetailScreen(
    navController: NavController,
    requestId: String
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { RequestRepository() }

    var request by remember { mutableStateOf<AccessRequest?>(null) }
    var role by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    fun load() {
        scope.launch {
            role = authRepository.getCurrentUserProfile()?.role.orEmpty()
            request = repository.getById(requestId)
        }
    }

    LaunchedEffect(requestId) { load() }

    LogiValScaffold(
        title = "Detalle",
        onBack = { navController.popBackStack() }
    ) { padding ->
        val item = request

        if (item == null) {
            CircularProgressIndicator(
                modifier = Modifier.padding(padding).padding(20.dp),
                color = LogiValGreen
            )
        } else {
            Column(
                modifier = Modifier.padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "Vehículo ${item.vehiclePlate}",
                            fontWeight = FontWeight.ExtraBold,
                            color = LogiValGreenDark,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        StatusChip(item.status)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Zona: ${item.pavilionId}", color = LogiValText)
                        Text("Motivo: ${item.reason}", color = LogiValText)
                        Text("Producto: ${item.productType}", color = LogiValText)
                        Text("Peso: ${item.estimatedWeight}", color = LogiValText)
                        Text("Permanencia: ${item.durationMinutes} min", color = LogiValText)
                        Text("Tarifa simulada: S/ ${"%.2f".format(item.calculatedFee)}", color = LogiValText)
                    }
                }

                if (role.isControlRole() && item.status == RequestStatus.PENDIENTE) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    repository.approve(item.requestId)
                                        .onSuccess {
                                            message = "Solicitud aprobada."
                                            load()
                                        }
                                        .onFailure {
                                            message = it.message ?: "Error."
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LogiValGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Aprobar")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    repository.reject(item.requestId)
                                        .onSuccess {
                                            message = "Solicitud rechazada."
                                            load()
                                        }
                                        .onFailure {
                                            message = it.message ?: "Error."
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LogiValRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rechazar")
                        }
                    }
                }

                if (item.status == RequestStatus.APROBADA || item.status == RequestStatus.EN_MERCADO) {
                    Button(
                        onClick = { navController.navigate(Routes.qrAccess(item.requestId)) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogiValGreen),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Ver QR")
                    }
                }

                if (message.isNotBlank()) {
                    Text(message, color = LogiValGreenDark)
                }
            }
        }
    }
}