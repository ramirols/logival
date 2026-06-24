package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.StatusChip
import com.cibertec.logivalgmml.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RequestListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { RequestRepository() }

    var requests by remember { mutableStateOf<List<AccessRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun load() {
        scope.launch {
            loading = true
            val user = authRepository.getCurrentUserProfile()
            val uid = authRepository.currentUid().orEmpty()
            val role = user?.role.orEmpty()

            repository.getRequests(uid, role)
                .onSuccess { requests = it }

            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    LogiValScaffold(
        title = "Solicitudes",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.REQUEST_FORM) },
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
                if (requests.isEmpty()) {
                    item {
                        Text("Aún no hay solicitudes.", color = LogiValText)
                    }
                }

                items(requests) { request ->
                    Card(
                        onClick = { navController.navigate(Routes.requestDetail(request.requestId)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Vehículo ${request.vehiclePlate}",
                                fontWeight = FontWeight.Bold,
                                color = LogiValGreenDark
                            )
                            Text("Zona: ${request.pavilionId}", color = LogiValText)
                            Text("Motivo: ${request.reason}", color = LogiValText.copy(alpha = 0.75f))
                            Spacer(modifier = Modifier.height(8.dp))
                            StatusChip(request.status)
                        }
                    }
                }
            }
        }
    }
}