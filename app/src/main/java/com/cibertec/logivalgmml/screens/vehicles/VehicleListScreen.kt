package com.cibertec.logivalgmml.screens.vehicles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.models.VehicleStatus
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import kotlinx.coroutines.launch

@Composable
fun VehicleListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val vehicleRepository = remember { VehicleRepository() }

    var user by remember { mutableStateOf<AppUser?>(null) }
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadVehicles() {
        scope.launch {
            loading = true
            errorMessage = ""

            user = authRepository.getCurrentUserProfile()

            val uid = authRepository.currentUid().orEmpty()
            val role = user?.role.orEmpty()

            vehicleRepository.getVehicles(uid, role)
                .onSuccess {
                    vehicles = it
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudieron cargar los vehículos."
                }

            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadVehicles()
    }

    LogiValScaffold(
        title = "Vehículos",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.VEHICLE_FORM) },
                containerColor = LogiValGreen,
                contentColor = LogiValWhite,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Registrar vehículo"
                    )
                },
                text = {
                    Text(
                        text = "Registrar",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LogiValBackground)
        ) {
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LogiValGreen,
                            strokeWidth = 3.dp
                        )
                    }
                }

                errorMessage.isNotBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = LogiValRed,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            top = 20.dp,
                            end = 20.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            VehicleHeader(
                                count = vehicles.size,
                                activeCount = vehicles.count { it.status == VehicleStatus.ACTIVO }
                            )
                        }

                        if (vehicles.isEmpty()) {
                            item {
                                EmptyVehiclesCard(
                                    onRegisterClick = {
                                        navController.navigate(Routes.VEHICLE_FORM)
                                    }
                                )
                            }
                        } else {
                            items(
                                items = vehicles,
                                key = { it.vehicleId }
                            ) { vehicle ->
                                VehicleCard(vehicle = vehicle)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleHeader(
    count: Int,
    activeCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValGreenDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.14f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Vehículos",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$count vehículos registrados",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$activeCount activos para solicitudes de ingreso",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun VehicleCard(vehicle: Vehicle) {
    val statusColor = when (vehicle.status) {
        VehicleStatus.ACTIVO -> LogiValGreen
        VehicleStatus.OBSERVADO -> Color(0xFFFF9800)
        VehicleStatus.INACTIVO -> LogiValRed
        else -> LogiValText.copy(alpha = 0.55f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = statusColor.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = statusColor.copy(alpha = 0.10f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Vehículo",
                            tint = statusColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = vehicle.plate.ifBlank { "Sin placa" },
                        color = LogiValGreenDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = vehicle.type.ifBlank { "Tipo no especificado" },
                        color = LogiValText.copy(alpha = 0.65f),
                        fontSize = 14.sp
                    )
                }

                VehicleStatusPill(
                    status = vehicle.status,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            VehicleInfoRow(
                icon = Icons.Default.Person,
                label = "Conductor",
                value = vehicle.driverName.ifBlank { "No registrado" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            VehicleInfoRow(
                icon = Icons.Default.Verified,
                label = "DNI",
                value = vehicle.driverDni.ifBlank { "No registrado" }
            )
        }
    }
}

@Composable
private fun VehicleInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = LogiValGreen.copy(alpha = 0.08f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = LogiValGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column {
            Text(
                text = label,
                color = LogiValText.copy(alpha = 0.50f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                color = LogiValText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun VehicleStatusPill(
    status: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.16f)
        )
    ) {
        Text(
            text = status.ifBlank { "SIN ESTADO" },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun EmptyVehiclesCard(
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(70.dp),
                shape = CircleShape,
                color = LogiValGreen.copy(alpha = 0.10f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Sin vehículos",
                        tint = LogiValGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Aún no hay vehículos registrados",
                color = LogiValGreenDark,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Registra un vehículo para crear solicitudes de ingreso al mercado.",
                color = LogiValText.copy(alpha = 0.68f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogiValGreen,
                    contentColor = LogiValWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Registrar vehículo",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}