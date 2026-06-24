package com.cibertec.logivalgmml.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.DashboardSummary
import com.cibertec.logivalgmml.repositories.DashboardRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.MetricCard
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val repository = remember { DashboardRepository() }
    var summary by remember { mutableStateOf<DashboardSummary?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repository.getSummary()
            .onSuccess { summary = it }
        loading = false
    }

    LogiValScaffold(
        title = "Dashboard",
        onBack = { navController.popBackStack() }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(color = LogiValGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MetricCard("Solicitudes pendientes", "${summary?.solicitudesPendientes ?: 0}", "Requieren aprobación")
                MetricCard("Vehículos dentro", "${summary?.vehiculosDentro ?: 0}", "Actualmente en mercado")
                MetricCard("Ingresos del día", "${summary?.ingresosHoy ?: 0}", "Entradas registradas hoy")
                MetricCard("Salidas del día", "${summary?.salidasHoy ?: 0}", "Salidas registradas hoy")
                MetricCard("Incidencias abiertas", "${summary?.incidenciasAbiertas ?: 0}", "Pendientes de atención")
                MetricCard("Permanencia promedio", "${summary?.permanenciaPromedio ?: 0} min", "Solicitudes finalizadas")
            }
        }
    }
}