package com.cibertec.logivalgmml.screens.requests

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.models.isControlRole
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
fun RequestDetailScreen(
    navController: NavController,
    requestId: String
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { RequestRepository() }

    var request by remember { mutableStateOf<AccessRequest?>(null) }
    var role by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var actionLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    fun load() {
        scope.launch {
            loading = true
            role = authRepository.getCurrentUserProfile()?.role.orEmpty()
            request = repository.getById(requestId)
            loading = false
        }
    }

    LaunchedEffect(requestId) {
        load()
    }

    LogiValScaffold(
        title = "Detalle",
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

                request == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontró la solicitud.",
                            color = LogiValRed,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                else -> {
                    val item = request!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RequestDetailHeader(item)

                        RequestDetailInfoCard(item)

                        if (role.isControlRole() && item.status == RequestStatus.PENDIENTE) {
                            ControlActionsCard(
                                loading = actionLoading,
                                onApprove = {
                                    scope.launch {
                                        actionLoading = true
                                        message = ""

                                        repository.approve(item.requestId)
                                            .onSuccess {
                                                message = "Solicitud aprobada correctamente."
                                                load()
                                            }
                                            .onFailure {
                                                message = it.message ?: "No se pudo aprobar."
                                            }

                                        actionLoading = false
                                    }
                                },
                                onReject = {
                                    scope.launch {
                                        actionLoading = true
                                        message = ""

                                        repository.reject(item.requestId)
                                            .onSuccess {
                                                message = "Solicitud rechazada correctamente."
                                                load()
                                            }
                                            .onFailure {
                                                message = it.message ?: "No se pudo rechazar."
                                            }

                                        actionLoading = false
                                    }
                                }
                            )
                        }

                        if (item.status == RequestStatus.APROBADA || item.status == RequestStatus.EN_MERCADO) {
                            Button(
                                onClick = {
                                    navController.navigate(Routes.qrAccess(item.requestId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LogiValGreen,
                                    contentColor = LogiValWhite
                                ),
                                shape = RoundedCornerShape(18.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "Ver QR",
                                    modifier = Modifier.size(21.dp)
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Text(
                                    text = "Ver código QR",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (message.isNotBlank()) {
                            MessageCard(message = message)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestDetailHeader(item: AccessRequest) {
    val statusColor = requestStatusColor(item.status)

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = LogiValWhite.copy(alpha = 0.14f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Vehículo",
                            tint = LogiValWhite,
                            modifier = Modifier.size(31.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.vehiclePlate.ifBlank { "Vehículo sin placa" },
                        color = LogiValWhite,
                        fontSize = 25.sp,
                        lineHeight = 29.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Solicitud ${item.requestId.take(8)}",
                        color = LogiValWhite.copy(alpha = 0.84f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RequestStatusPill(
                        status = item.status,
                        color = statusColor,
                        light = true
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestDetailInfoCard(item: AccessRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Información de la solicitud",
                color = LogiValGreenDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            RequestInfoRow(
                icon = Icons.Default.Place,
                label = "Zona o pabellón",
                value = item.pavilionId.ifBlank { "No especificado" }
            )

            RequestInfoRow(
                icon = Icons.Default.Assignment,
                label = "Motivo",
                value = item.reason.ifBlank { "Sin motivo registrado" }
            )

            RequestInfoRow(
                icon = Icons.Default.Inventory,
                label = "Producto",
                value = item.productType.ifBlank { "No especificado" }
            )

            RequestInfoRow(
                icon = Icons.Default.Scale,
                label = "Peso estimado",
                value = item.estimatedWeight.ifBlank { "No especificado" }
            )

            RequestInfoRow(
                icon = Icons.Default.Store,
                label = "Comerciante asociado",
                value = item.merchantName.ifBlank { "No asociado" }
            )

            RequestInfoRow(
                icon = Icons.Default.Timer,
                label = "Fecha de solicitud",
                value = formatDate(item.requestedAt)
            )

            RequestInfoRow(
                icon = Icons.Default.Timer,
                label = "Permanencia",
                value = "${item.durationMinutes} min"
            )

            RequestInfoRow(
                icon = Icons.Default.AttachMoney,
                label = "Tarifa simulada",
                value = "S/ ${"%.2f".format(item.calculatedFee)}"
            )
        }
    }
}

@Composable
private fun ControlActionsCard(
    loading: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Acciones de control",
                color = LogiValGreenDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Esta solicitud está pendiente. Puedes aprobarla o rechazarla según la validación operativa.",
                color = LogiValText.copy(alpha = 0.66f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    enabled = !loading,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogiValGreen,
                        contentColor = LogiValWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Aprobar",
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Aprobar",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onReject,
                    enabled = !loading,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = LogiValRed.copy(alpha = 0.35f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LogiValRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Rechazar",
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Rechazar",
                        fontWeight = FontWeight.Bold
                    )
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
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color = LogiValGreen.copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = LogiValGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = LogiValText.copy(alpha = 0.50f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = LogiValText,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RequestStatusPill(
    status: String,
    color: Color,
    light: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (light) LogiValWhite.copy(alpha = 0.18f) else color.copy(alpha = 0.10f),
        border = BorderStroke(
            width = 1.dp,
            color = if (light) LogiValWhite.copy(alpha = 0.24f) else color.copy(alpha = 0.16f)
        )
    ) {
        Text(
            text = status.ifBlank { "PENDIENTE" },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = if (light) LogiValWhite else color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun MessageCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValGreen.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.14f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = LogiValGreenDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
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