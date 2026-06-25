package com.cibertec.logivalgmml.screens.qr

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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.QrCodeBox
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QrAccessScreen(
    navController: NavController,
    requestId: String
) {
    val repository = remember { RequestRepository() }

    var request by remember { mutableStateOf<AccessRequest?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(requestId) {
        request = repository.getById(requestId)
        loading = false
    }

    LogiValScaffold(
        title = "QR de acceso",
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
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QrHeader(item = item)

                        QrCard(item = item)

                        QrInfoCard(item = item)

                        InstructionCard()

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QrHeader(item: AccessRequest) {
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
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR",
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
                        text = "Código de acceso",
                        color = LogiValWhite.copy(alpha = 0.86f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = item.vehiclePlate.ifBlank { "Vehículo sin placa" },
                        color = LogiValWhite,
                        fontSize = 25.sp,
                        lineHeight = 29.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    QrStatusPill(
                        status = item.status,
                        color = qrStatusColor(item.status),
                        light = true
                    )
                }
            }
        }
    }
}

@Composable
private fun QrCard(item: AccessRequest) {
    val qrValue = item.qrCode.ifBlank { item.requestId }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = LogiValBackground,
                border = BorderStroke(
                    width = 1.dp,
                    color = LogiValGreen.copy(alpha = 0.10f)
                )
            ) {
                Box(
                    modifier = Modifier.padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeBox(text = qrValue)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ID de validación",
                color = LogiValText.copy(alpha = 0.55f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = qrValue,
                color = LogiValGreenDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun QrInfoCard(item: AccessRequest) {
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
                text = "Detalle de acceso",
                color = LogiValGreenDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            QrInfoRow(
                icon = Icons.Default.DirectionsCar,
                label = "Vehículo",
                value = item.vehiclePlate.ifBlank { "No especificado" }
            )

            QrInfoRow(
                icon = Icons.Default.Place,
                label = "Zona",
                value = item.pavilionId.ifBlank { "No especificada" }
            )

            QrInfoRow(
                icon = Icons.Default.Store,
                label = "Comerciante",
                value = item.merchantName.ifBlank { "No asociado" }
            )

            QrInfoRow(
                icon = Icons.Default.Timer,
                label = "Fecha de solicitud",
                value = formatQrDate(item.requestedAt)
            )

            QrInfoRow(
                icon = Icons.Default.Verified,
                label = "Estado",
                value = item.status.ifBlank { "PENDIENTE" }
            )
        }
    }
}

@Composable
private fun InstructionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValGreen.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.14f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = LogiValGreen.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Información",
                        tint = LogiValGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Text(
                text = "El personal de control debe validar este código para registrar el ingreso o la salida del vehículo.",
                color = LogiValGreenDark,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QrInfoRow(
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
private fun QrStatusPill(
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

private fun qrStatusColor(status: String): Color {
    return when (status) {
        RequestStatus.PENDIENTE -> Color(0xFFFF9800)
        RequestStatus.APROBADA -> LogiValGreen
        RequestStatus.RECHAZADA -> LogiValRed
        RequestStatus.EN_MERCADO -> LogiValGreenDark
        RequestStatus.FINALIZADA -> LogiValText.copy(alpha = 0.55f)
        else -> LogiValGreen
    }
}

private fun formatQrDate(time: Long): String {
    if (time <= 0L) return "Sin fecha"

    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
        formatter.format(Date(time))
    } catch (e: Exception) {
        "Sin fecha"
    }
}