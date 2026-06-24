package com.cibertec.logivalgmml.screens.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.StatusChip
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VehicleListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val vehicleRepository = remember { VehicleRepository() }

    var user by remember { mutableStateOf<AppUser?>(null) }
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun load() {
        scope.launch {
            loading = true
            user = authRepository.getCurrentUserProfile()
            val uid = authRepository.currentUid().orEmpty()
            val role = user?.role.orEmpty()

            vehicleRepository.getVehicles(uid, role)
                .onSuccess { vehicles = it }

            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    LogiValScaffold(
        title = "Vehículos",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.VEHICLE_FORM) },
                containerColor = LogiValGreen
            ) {
                Text("+", color = LogiValWhite)
            }
        }
    ) { padding ->
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(padding).padding(20.dp),
                color = LogiValGreen
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (vehicles.isEmpty()) {
                    item {
                        Text("Aún no hay vehículos registrados.", color = LogiValText)
                    }
                }

                items(vehicles) { vehicle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = vehicle.plate,
                                fontWeight = FontWeight.ExtraBold,
                                color = LogiValGreenDark,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("Tipo: ${vehicle.type}", color = LogiValText)
                            Text("Conductor: ${vehicle.driverName}", color = LogiValText.copy(alpha = 0.75f))
                            Spacer(modifier = Modifier.height(8.dp))
                            StatusChip(vehicle.status)
                        }
                    }
                }
            }
        }
    }
}