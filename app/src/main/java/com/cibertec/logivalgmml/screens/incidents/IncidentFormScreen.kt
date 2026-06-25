package com.cibertec.logivalgmml.screens.incidents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.IncidentRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IncidentFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val repository = remember { IncidentRepository() }

    var requestId by remember { mutableStateOf("") }
    var vehicleId by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val quickTypes = listOf(
        "Daño",
        "Demora",
        "Congestión",
        "Acceso",
        "Carga",
        "Descarga"
    )

    LogiValScaffold(
        title = "Nueva incidencia",
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
                IncidentFormHeader()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = LogiValWhite),
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
                            text = "Datos de la incidencia",
                            color = LogiValGreenDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Registra el problema para que pueda ser revisado por el personal correspondiente.",
                            color = LogiValText.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Text(
                            text = "Tipo rápido",
                            color = LogiValGreenDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickTypes.forEach { item ->
                                AssistChip(
                                    onClick = {
                                        type = item
                                        message = ""
                                    },
                                    label = {
                                        Text(
                                            text = item,
                                            fontWeight = if (type == item) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Medium
                                            }
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (type == item) {
                                            LogiValGreen.copy(alpha = 0.14f)
                                        } else {
                                            LogiValWhite
                                        },
                                        labelColor = if (type == item) {
                                            LogiValGreenDark
                                        } else {
                                            LogiValText.copy(alpha = 0.70f)
                                        }
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (type == item) {
                                            LogiValGreen.copy(alpha = 0.25f)
                                        } else {
                                            LogiValText.copy(alpha = 0.12f)
                                        }
                                    )
                                )
                            }
                        }

                        IncidentTextField(
                            value = type,
                            onValueChange = {
                                type = it
                                message = ""
                            },
                            label = "Tipo de incidencia",
                            placeholder = "Daño, demora, congestión...",
                            icon = Icons.Default.Warning,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        IncidentTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                message = ""
                            },
                            label = "Descripción",
                            placeholder = "Describe brevemente lo ocurrido",
                            icon = Icons.Default.Subject,
                            minLines = 4,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        IncidentTextField(
                            value = requestId,
                            onValueChange = {
                                requestId = it
                                message = ""
                            },
                            label = "Solicitud ID opcional",
                            placeholder = "ID de solicitud vinculada",
                            icon = Icons.Default.Assignment,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        IncidentTextField(
                            value = vehicleId,
                            onValueChange = {
                                vehicleId = it
                                message = ""
                            },
                            label = "Vehículo ID opcional",
                            placeholder = "ID del vehículo vinculado",
                            icon = Icons.Default.DirectionsCar,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        IncidentTextField(
                            value = photoUrl,
                            onValueChange = {
                                photoUrl = it
                                message = ""
                            },
                            label = "URL de evidencia opcional",
                            placeholder = "https://...",
                            icon = Icons.Default.Image,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            )
                        )

                        if (message.isNotBlank()) {
                            Text(
                                text = message,
                                color = LogiValRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                loading = true
                                message = ""

                                scope.launch {
                                    val uid = authRepository.currentUid().orEmpty()

                                    repository.createIncident(
                                        uid = uid,
                                        requestId = requestId,
                                        vehicleId = vehicleId,
                                        type = type,
                                        description = description,
                                        photoUrl = photoUrl
                                    ).onSuccess {
                                        navController.popBackStack()
                                    }.onFailure {
                                        message = it.message ?: "No se pudo registrar la incidencia."
                                    }

                                    loading = false
                                }
                            },
                            enabled = !loading,
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
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(23.dp),
                                    color = LogiValWhite,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Guardar",
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Text(
                                    text = "Registrar incidencia",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun IncidentFormHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = LogiValGreenDark),
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
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = LogiValWhite.copy(alpha = 0.14f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = "Incidencia",
                            tint = LogiValWhite,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reportar problema",
                        color = LogiValWhite,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Registra daños, demoras o situaciones ocurridas durante la operación.",
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
private fun IncidentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    minLines: Int = 1,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(18.dp),
        leadingIcon = {
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
        },
        keyboardOptions = keyboardOptions,
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