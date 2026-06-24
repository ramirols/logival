package com.cibertec.logivalgmml.screens.incidents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.Incident
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.IncidentRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.StatusChip
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun IncidentListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { IncidentRepository() }

    var incidents by remember { mutableStateOf<List<Incident>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = authRepository.getCurrentUserProfile()
        val uid = authRepository.currentUid().orEmpty()

        repository.getIncidents(uid, user?.role.orEmpty())
            .onSuccess { incidents = it }

        loading = false
    }

    LogiValScaffold(
        title = "Incidencias",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.INCIDENT_FORM) },
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
                if (incidents.isEmpty()) {
                    item {
                        Text("Aún no hay incidencias.", color = LogiValText)
                    }
                }

                items(incidents) { incident ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = incident.type,
                                fontWeight = FontWeight.Bold,
                                color = LogiValGreenDark
                            )
                            Text(incident.description, color = LogiValText)
                            if (incident.vehicleId.isNotBlank()) {
                                Text("Vehículo ID: ${incident.vehicleId}", color = LogiValText.copy(alpha = 0.65f))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            StatusChip(incident.status)
                        }
                    }
                }
            }
        }
    }
}