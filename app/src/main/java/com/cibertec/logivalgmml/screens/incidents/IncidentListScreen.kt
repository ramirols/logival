package com.cibertec.logivalgmml.screens.incidents

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.cibertec.logivalgmml.models.Incident
import com.cibertec.logivalgmml.models.IncidentStatus
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.IncidentRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import kotlinx.coroutines.launch

@Composable
fun IncidentListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { IncidentRepository() }

    var incidents by remember { mutableStateOf<List<Incident>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadIncidents() {
        scope.launch {
            loading = true
            errorMessage = ""

            val user = authRepository.getCurrentUserProfile()
            val uid = authRepository.currentUid().orEmpty()

            repository.getIncidents(uid, user?.role.orEmpty())
                .onSuccess {
                    incidents = it
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudieron cargar las incidencias."
                }

            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadIncidents()
    }

    LogiValScaffold(
        title = "Incidencias",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.INCIDENT_FORM) },
                containerColor = LogiValGreen,
                contentColor = LogiValWhite,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva incidencia"
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
                            IncidentHeader(
                                total = incidents.size,
                                open = incidents.count {
                                    it.status == IncidentStatus.ABIERTA ||
                                            it.status == IncidentStatus.EN_REVISION
                                }
                            )
                        }

                        if (incidents.isEmpty()) {
                            item {
                                EmptyIncidentsCard(
                                    onRegisterClick = {
                                        navController.navigate(Routes.INCIDENT_FORM)
                                    }
                                )
                            }
                        } else {
                            items(incidents) { incident ->
                                IncidentCard(incident = incident)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IncidentHeader(
    total: Int,
    open: Int
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
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "Incidencias",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$total incidencias registradas",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$open pendientes de atención",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun IncidentCard(incident: Incident) {
    val statusColor = when (incident.status) {
        IncidentStatus.ABIERTA -> LogiValRed
        IncidentStatus.EN_REVISION -> Color(0xFFFF9800)
        IncidentStatus.RESUELTA -> LogiValGreen
        IncidentStatus.DESCARTADA -> LogiValText.copy(alpha = 0.55f)
        else -> LogiValGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LogiValWhite),
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
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Incidencia",
                            tint = statusColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incident.type.ifBlank { "Incidencia" },
                        color = LogiValGreenDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = incident.description.ifBlank { "Sin descripción registrada." },
                        color = LogiValText.copy(alpha = 0.70f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                IncidentStatusPill(
                    status = incident.status,
                    color = statusColor
                )
            }

            if (incident.vehicleId.isNotBlank() || incident.requestId.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                if (incident.vehicleId.isNotBlank()) {
                    IncidentInfoRow(
                        icon = Icons.Default.DirectionsCar,
                        label = "Vehículo",
                        value = incident.vehicleId
                    )
                }

                if (incident.requestId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    IncidentInfoRow(
                        icon = Icons.Default.Assignment,
                        label = "Solicitud",
                        value = incident.requestId
                    )
                }
            }
        }
    }
}

@Composable
private fun IncidentInfoRow(
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
            Box(contentAlignment = Alignment.Center) {
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
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IncidentStatusPill(
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
            text = status.ifBlank { "ABIERTA" },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun EmptyIncidentsCard(
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = LogiValWhite),
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
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "Sin incidencias",
                        tint = LogiValGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "No hay incidencias registradas",
                color = LogiValGreenDark,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuando ocurra un problema operativo, podrás registrarlo desde esta sección.",
                color = LogiValText.copy(alpha = 0.68f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}