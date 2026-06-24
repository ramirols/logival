package com.cibertec.logivalgmml.screens.qr

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AccessRequest
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.components.QrCodeBox
import com.cibertec.logivalgmml.ui.components.StatusChip
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValText

@Composable
fun QrAccessScreen(
    navController: NavController,
    requestId: String
) {
    val repository = remember { RequestRepository() }
    var request by remember { mutableStateOf<AccessRequest?>(null) }

    LaunchedEffect(requestId) {
        request = repository.getById(requestId)
    }

    LogiValScaffold(
        title = "QR de acceso",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val item = request

            if (item == null) {
                Text("Cargando QR...")
            } else {
                Text(
                    text = "Vehículo ${item.vehiclePlate}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LogiValGreenDark
                )

                Spacer(modifier = Modifier.height(8.dp))
                StatusChip(item.status)
                Spacer(modifier = Modifier.height(24.dp))

                QrCodeBox(text = item.qrCode.ifBlank { item.requestId })

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "El personal de control debe validar este código para registrar ingreso o salida.",
                    color = LogiValText.copy(alpha = 0.75f)
                )
            }
        }
    }
}