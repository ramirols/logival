package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
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
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RequestListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { RequestRepository() }

    var requests by remember { mutableStateOf<List<AccessRequest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadRequests() {
        scope.launch {
            loading = true
            errorMessage = ""

            val user = authRepository.getCurrentUserProfile()
            val uid = authRepository.currentUid().orEmpty()
            val role = user?.role.orEmpty()

            repository.getRequests(uid, role)
                .onSuccess {
                    requests = it
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudieron cargar las solicitudes."
                }

            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadRequests()
    }

    LogiValScaffold(
        title = "Solicitudes",
        onBack = { navController.popBackStack() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.REQUEST_FORM) },
                containerColor = LogiValGreen,
                contentColor = LogiValWhite,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva solicitud"
                    )
                },
                text = {
                    Text(
                        text = "Nueva",
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
                            RequestHeader(
                                total = requests.size,
                                pending = requests.count { it.status == RequestStatus.PENDIENTE },
                                approved = requests.count { it.status == RequestStatus.APROBADA }
                            )
                        }

                        if (requests.isEmpty()) {
                            item {
                                EmptyRequestsCard(
                                    onCreateClick = {
                                        navController.navigate(Routes.REQUEST_FORM)
                                    }
                                )
                            }
                        } else {
                            items(requests) { request ->
                                RequestCard(
                                    request = request,
                                    onClick = {
                                        navController.navigate(Routes.requestDetail(request.requestId))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestHeader(
    total: Int,
    pending: Int,
    approved: Int
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
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Solicitudes",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$total solicitudes registradas",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$pending pendientes  •  $approved aprobadas",
                    color = Color.White.copy(alpha = 0.84f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: AccessRequest,
    onClick: () -> Unit
) {
    val statusColor = requestStatusColor(request.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Vehículo",
                            tint = statusColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.vehiclePlate.ifBlank { "Vehículo sin placa" },
                        color = LogiValGreenDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = request.reason.ifBlank { "Sin motivo registrado" },
                        color = LogiValText.copy(alpha = 0.68f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                RequestStatusPill(
                    status = request.status,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RequestInfoRow(
                icon = Icons.Default.Place,
                label = "Zona",
                value = request.pavilionId.ifBlank { "No especificada" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            RequestInfoRow(
                icon = Icons.Default.Store,
                label = "Comerciante",
                value = request.merchantName.ifBlank { "No asociado" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            RequestInfoRow(
                icon = Icons.Default.Timer,
                label = "Fecha",
                value = formatDate(request.requestedAt)
            )

            if (request.status == RequestStatus.APROBADA || request.status == RequestStatus.EN_MERCADO) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = LogiValGreen.copy(alpha = 0.08f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = LogiValGreen.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR",
                            tint = LogiValGreen,
                            modifier = Modifier.size(21.dp)
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = "QR disponible para validación",
                            color = LogiValGreenDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestInfoRow(
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
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RequestStatusPill(
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
            text = status.ifBlank { "PENDIENTE" },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun EmptyRequestsCard(
    onCreateClick: () -> Unit
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
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Sin solicitudes",
                        tint = LogiValGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Aún no hay solicitudes",
                color = LogiValGreenDark,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crea una solicitud para registrar el ingreso de un vehículo al mercado.",
                color = LogiValText.copy(alpha = 0.68f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

private fun requestStatusColor(status: String): Color {
    return when (status) {
        RequestStatus.PENDIENTE -> Color(0xFFFF9800)
        RequestStatus.APROBADA -> LogiValGreen
        RequestStatus.RECHAZADA -> LogiValRed
        RequestStatus.EN_MERCADO -> LogiValGreenDark
        RequestStatus.FINALIZADA -> LogiValText.copy(alpha = 0.55f)
        else -> LogiValGreen
    }
}

private fun formatDate(time: Long): String {
    if (time <= 0L) return "Sin fecha"

    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
        formatter.format(Date(time))
    } catch (e: Exception) {
        "Sin fecha"
    }
}