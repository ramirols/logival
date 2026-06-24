package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.UserRole
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.models.normalizedRole
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import kotlinx.coroutines.launch

@Composable
fun RequestFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val vehicleRepository = remember { VehicleRepository() }
    val requestRepository = remember { RequestRepository() }

    var currentUser by remember { mutableStateOf<AppUser?>(null) }

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }

    var merchants by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var selectedMerchant by remember { mutableStateOf<AppUser?>(null) }

    var pavilion by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var estimatedWeight by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val user = authRepository.getCurrentUserProfile()
        currentUser = user

        val uid = authRepository.currentUid().orEmpty()
        val role = user?.role.orEmpty()

        vehicleRepository.getVehiclesForRequest(uid, role)
            .onSuccess {
                vehicles = it
                selectedVehicle = it.firstOrNull()
            }
            .onFailure {
                message = it.message ?: "No se pudieron cargar los vehículos."
            }

        authRepository.getUsersByRole(UserRole.COMERCIANTE)
            .onSuccess {
                merchants = it
                selectedMerchant = it.firstOrNull()
            }
    }

    val role = currentUser?.role.orEmpty()
    val isComerciante = role.normalizedRole() == UserRole.COMERCIANTE

    LogiValScaffold(
        title = "Nueva solicitud",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Selecciona vehículo",
                color = LogiValGreenDark,
                fontWeight = FontWeight.Bold
            )

            if (vehicles.isEmpty()) {
                Text(
                    text = "No hay vehículos disponibles. Primero registra un vehículo.",
                    color = LogiValRed
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(vehicles) { vehicle ->
                        FilterChip(
                            selected = selectedVehicle?.vehicleId == vehicle.vehicleId,
                            onClick = { selectedVehicle = vehicle },
                            label = { Text(vehicle.plate) }
                        )
                    }
                }
            }

            Text(
                text = "Comerciante asociado",
                color = LogiValGreenDark,
                fontWeight = FontWeight.Bold
            )

            if (isComerciante) {
                Text(
                    text = currentUser?.name ?: "Comerciante actual",
                    color = LogiValText.copy(alpha = 0.75f)
                )
            } else {
                if (merchants.isEmpty()) {
                    Text(
                        text = "No hay comerciantes registrados. Crea un usuario con rol COMERCIANTE.",
                        color = LogiValRed
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(merchants) { merchant ->
                            FilterChip(
                                selected = selectedMerchant?.uid == merchant.uid,
                                onClick = { selectedMerchant = merchant },
                                label = {
                                    Text(
                                        text = merchant.name.ifBlank { merchant.email }
                                    )
                                }
                            )
                        }
                    }
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

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    loading = true
                    message = ""

                    scope.launch {
                        val uid = authRepository.currentUid().orEmpty()
                        val vehicle = selectedVehicle

                        if (vehicle == null) {
                            message = "Primero selecciona un vehículo."
                            loading = false
                            return@launch
                        }

                        val merchantUid: String
                        val merchantName: String

                        if (isComerciante) {
                            merchantUid = currentUser?.uid.orEmpty()
                            merchantName = currentUser?.name.orEmpty()
                        } else {
                            val merchant = selectedMerchant

                            if (merchant == null) {
                                message = "Selecciona el comerciante asociado."
                                loading = false
                                return@launch
                            }

                            merchantUid = merchant.uid
                            merchantName = merchant.name.ifBlank { merchant.email }
                        }

                        requestRepository.createRequest(
                            uid = uid,
                            vehicle = vehicle,
                            pavilionId = pavilion,
                            reason = reason,
                            productType = productType,
                            estimatedWeight = estimatedWeight,
                            merchantUid = merchantUid,
                            merchantName = merchantName
                        ).onSuccess {
                            navController.popBackStack()
                        }.onFailure {
                            message = it.message ?: "No se pudo crear la solicitud."
                        }

                        loading = false
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogiValGreen
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (loading) "Enviando..." else "Enviar solicitud",
                    fontWeight = FontWeight.Bold
                )
            }

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = LogiValRed
                )
            }
        }
    }
}