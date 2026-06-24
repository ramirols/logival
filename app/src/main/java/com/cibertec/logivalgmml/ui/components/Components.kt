package com.cibertec.logivalgmml.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cibertec.logivalgmml.models.RequestStatus
import com.cibertec.logivalgmml.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogiValScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = LogiValBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = LogiValGreenDark
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text("Atrás", color = LogiValGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LogiValBackground
                )
            )
        },
        floatingActionButton = floatingActionButton ?: {}
    ) { padding ->
        content(padding)
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, color = LogiValText.copy(alpha = 0.70f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = LogiValGreenDark
            )
            Text(description, color = LogiValText.copy(alpha = 0.65f))
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        RequestStatus.PENDIENTE -> LogiValYellow
        RequestStatus.APROBADA -> LogiValGreen
        RequestStatus.RECHAZADA -> LogiValRed
        RequestStatus.EN_MERCADO -> LogiValGreenDark
        RequestStatus.FINALIZADA -> LogiValText
        else -> LogiValGreen
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QrCodeBox(
    text: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(text) {
        createQrBitmap(text)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Código QR",
                modifier = Modifier.size(230.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = text,
                color = LogiValText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun createQrBitmap(content: String): Bitmap {
    val size = 700
    val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x,
                y,
                if (bits[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }

    return bitmap
}