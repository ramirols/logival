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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

@Composable
fun QrScannerScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val requestRepository = remember { RequestRepository() }

    var qrCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf<Boolean?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LogiValScaffold(
        title = "Validar QR",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LogiValBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ScannerHeader()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
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
                            text = "Código de validación",
                            color = LogiValGreenDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Para el MVP, pega el código QR o requestId generado por la solicitud aprobada.",
                            color = LogiValText.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        QrScannerTextField(
                            value = qrCode,
                            onValueChange = {
                                qrCode = it
                                message = ""
                                success = null
                            }
                        )

                        InfoBox()

                        Button(
                            onClick = {
                                isLoading = true
                                message = ""
                                success = null

                                scope.launch {
                                    val controlUid = authRepository.currentUid().orEmpty()

                                    requestRepository.validateQr(
                                        requestId = qrCode.trim(),
                                        controlUid = controlUid
                                    ).onSuccess {
                                        message = it
                                        success = true
                                        qrCode = ""
                                    }.onFailure {
                                        message = it.message ?: "No se pudo validar el QR."
                                        success = false
                                    }

                                    isLoading = false
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LogiValGreen,
                                contentColor = LogiValWhite
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(23.dp),
                                    color = LogiValWhite,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Validar acceso",
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Text(
                                    text = "Validar acceso",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (message.isNotBlank()) {
                    ValidationMessageCard(
                        message = message,
                        success = success == true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ScannerHeader() {
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
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Escáner QR",
                            tint = LogiValWhite,
                            modifier = Modifier.size(31.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Control de acceso",
                        color = LogiValWhite,
                        fontSize = 23.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Registra ingreso o salida validando el QR de la solicitud.",
                        color = LogiValWhite.copy(alpha = 0.84f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QrScannerTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Código QR / requestId") },
        placeholder = { Text("Pega aquí el código generado") },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        leadingIcon = {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = LogiValGreen.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Código QR",
                        tint = LogiValGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LogiValGreen,
            unfocusedBorderColor = LogiValText.copy(alpha = 0.16f),
            focusedLabelColor = LogiValGreen,
            unfocusedLabelColor = LogiValText.copy(alpha = 0.58f),
            cursorColor = LogiValGreen,
            focusedContainerColor = LogiValWhite,
            unfocusedContainerColor = LogiValWhite
        )
    )
}

@Composable
private fun InfoBox() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LogiValGreen.copy(alpha = 0.08f),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.14f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = LogiValGreen.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Información",
                        tint = LogiValGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = "Primera validación: registra ingreso. Segunda validación: registra salida y calcula permanencia.",
                color = LogiValGreenDark,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ValidationMessageCard(
    message: String,
    success: Boolean
) {
    val color = if (success) LogiValGreen else LogiValRed
    val icon = if (success) Icons.Default.CheckCircle else Icons.Default.Error

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.14f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Resultado",
                        tint = color,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Text(
                text = message,
                color = color,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}