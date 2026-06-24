package com.cibertec.logivalgmml.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.DashboardSummary
import com.cibertec.logivalgmml.repositories.DashboardRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite

private data class DashboardMetric(
    val title: String,
    val value: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun DashboardScreen(navController: NavController) {
    val repository = remember { DashboardRepository() }

    var summary by remember { mutableStateOf<DashboardSummary?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        repository.getSummary()
            .onSuccess {
                summary = it
            }
            .onFailure {
                errorMessage = it.message ?: "No se pudo cargar el dashboard."
            }

        loading = false
    }

    LogiValScaffold(
        title = "Dashboard",
        onBack = { navController.popBackStack() }
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
                    DashboardContent(summary = summary ?: DashboardSummary())
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(summary: DashboardSummary) {
    val metrics = listOf(
        DashboardMetric(
            title = "Pendientes",
            value = "${summary.solicitudesPendientes}",
            description = "Solicitudes por aprobar",
            icon = Icons.Default.Assignment,
            color = LogiValGreen
        ),
        DashboardMetric(
            title = "Dentro",
            value = "${summary.vehiculosDentro}",
            description = "Vehículos en mercado",
            icon = Icons.Default.DirectionsCar,
            color = LogiValGreenDark
        ),
        DashboardMetric(
            title = "Ingresos",
            value = "${summary.ingresosHoy}",
            description = "Entradas de hoy",
            icon = Icons.Default.Login,
            color = LogiValGreen
        ),
        DashboardMetric(
            title = "Salidas",
            value = "${summary.salidasHoy}",
            description = "Salidas de hoy",
            icon = Icons.Default.Logout,
            color = LogiValGreenDark
        ),
        DashboardMetric(
            title = "Incidencias",
            value = "${summary.incidenciasAbiertas}",
            description = "Casos abiertos",
            icon = Icons.Default.ReportProblem,
            color = LogiValRed
        ),
        DashboardMetric(
            title = "Promedio",
            value = "${summary.permanenciaPromedio} min",
            description = "Permanencia media",
            icon = Icons.Default.AccessTime,
            color = LogiValGreen
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DashboardHeader(summary = summary)

        Text(
            text = "Indicadores operativos",
            color = LogiValGreenDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        MetricGrid(metrics = metrics)

        OperationalStatusCard(summary = summary)

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DashboardHeader(summary: DashboardSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValGreenDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            LogiValGreenDark,
                            LogiValGreen
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Text(
                    text = "Resumen del mercado",
                    color = Color.White.copy(alpha = 0.86f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${summary.vehiculosDentro} vehículos dentro",
                    color = Color.White,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresos: ${summary.ingresosHoy}  •  Salidas: ${summary.salidasHoy}",
                    color = Color.White.copy(alpha = 0.86f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MetricGrid(metrics: List<DashboardMetric>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        metrics.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { metric ->
                    DashboardMetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    metric: DashboardMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = metric.color.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(15.dp),
                color = metric.color.copy(alpha = 0.10f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = metric.icon,
                        contentDescription = metric.title,
                        tint = metric.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = metric.value,
                color = metric.color,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = metric.title,
                color = LogiValGreenDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = metric.description,
                color = LogiValText.copy(alpha = 0.62f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun OperationalStatusCard(summary: DashboardSummary) {
    val hasAlerts = summary.solicitudesPendientes > 0 || summary.incidenciasAbiertas > 0

    val statusText = if (hasAlerts) {
        "Hay elementos pendientes de atención"
    } else {
        "Operación estable"
    }

    val statusDescription = if (hasAlerts) {
        "Revisa solicitudes pendientes e incidencias abiertas para mantener el flujo operativo."
    } else {
        "No hay solicitudes pendientes ni incidencias abiertas en este momento."
    }

    val statusColor = if (hasAlerts) LogiValRed else LogiValGreen

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = statusColor.copy(alpha = 0.14f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "Estado operativo",
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = statusDescription,
                    color = LogiValText.copy(alpha = 0.68f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}