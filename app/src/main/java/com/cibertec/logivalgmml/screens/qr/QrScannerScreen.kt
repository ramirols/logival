package com.cibertec.logivalgmml.screens.qr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import kotlinx.coroutines.launch

@Composable
fun QrScannerScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val requestRepository = remember { RequestRepository() }

    var qrCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LogiValScaffold(
        title = "Validar QR",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                text = "Para el MVP, pega aquí el código del QR o requestId. Luego puedes cambiar esto por cámara.",
                color = LogiValText.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = qrCode,
                onValueChange = { qrCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Código QR") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    message = ""

                    scope.launch {
                        val controlUid = authRepository.currentUid().orEmpty()

                        requestRepository.validateQr(
                            requestId = qrCode.trim(),
                            controlUid = controlUid
                        ).onSuccess {
                            message = it
                            qrCode = ""
                        }.onFailure {
                            message = it.message ?: "No se pudo validar el QR."
                        }

                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogiValGreen
                )
            ) {
                Text(
                    text = if (isLoading) "Validando..." else "Validar acceso"
                )
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message,
                    color = if (
                        message.contains("registrado", ignoreCase = true) ||
                        message.contains("registrada", ignoreCase = true)
                    ) {
                        LogiValGreenDark
                    } else {
                        LogiValRed
                    }
                )
            }
        }
    }
}