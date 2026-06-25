package com.cibertec.logivalgmml.screens.requests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Subject
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
import androidx.compose.runtime.LaunchedEffect
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
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.UserRole
import com.cibertec.logivalgmml.models.Vehicle
import com.cibertec.logivalgmml.models.normalizedRole
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.repositories.RequestRepository
import com.cibertec.logivalgmml.repositories.VehicleRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite
import kotlinx.coroutines.launch

@Composable
fun RequestFormScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val vehicleRepository = remember { VehicleRepository() }
    val requestRepository = remember { RequestRepository() }

    var currentUser by remember { mutableStateOf<AppUser?>(null) }

    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }

    var merchants by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var selectedMerchant by remember { mutableStateOf<AppUser?>(null) }

    var pavilion by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var estimatedWeight by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val user = authRepository.getCurrentUserProfile()
        currentUser = user

        val uid = authRepository.currentUid().orEmpty()
        val role = user?.role.orEmpty()

        vehicleRepository.getVehiclesForRequest(uid, role)
            .onSuccess {
                vehicles = it
                selectedVehicle = it.firstOrNull()
            }
            .onFailure {
                message = it.message ?: "No se pudieron cargar los vehículos."
            }

        authRepository.getUsersByRole(UserRole.COMERCIANTE)
            .onSuccess {
                merchants = it
                selectedMerchant = it.firstOrNull()
            }
    }

    val role = currentUser?.role.orEmpty()
    val isComerciante = role.normalizedRole() == UserRole.COMERCIANTE

    LogiValScaffold(
        title = "Nueva solicitud",
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
                RequestFormHeader()

                SelectionCard(
                    title = "Vehículo",
                    subtitle = "Selecciona el vehículo que ingresará al mercado.",
                    icon = Icons.Default.DirectionsCar
                ) {
                    if (vehicles.isEmpty()) {
                        WarningText("No hay vehículos disponibles. Primero registra un vehículo.")
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(vehicles) { vehicle ->
                                VehicleChoiceChip(
                                    vehicle = vehicle,
                                    selected = selectedVehicle?.vehicleId == vehicle.vehicleId,
                                    onClick = { selectedVehicle = vehicle }
                                )
                            }
                        }
                    }
                }

                SelectionCard(
                    title = "Comerciante asociado",
                    subtitle = "Vincula la solicitud con la operación comercial correspondiente.",
                    icon = Icons.Default.Store
                ) {
                    if (isComerciante) {
                        CurrentMerchantCard(
                            name = currentUser?.name ?: "Comerciante actual",
                            email = currentUser?.email.orEmpty()
                        )
                    } else {
                        if (merchants.isEmpty()) {
                            WarningText("No hay comerciantes registrados. Crea un usuario con rol COMERCIANTE.")
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(merchants) { merchant ->
                                    MerchantChoiceChip(
                                        merchant = merchant,
                                        selected = selectedMerchant?.uid == merchant.uid,
                                        onClick = { selectedMerchant = merchant }
                                    )
                                }
                            }
                        }
                    }
                }

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
                            text = "Datos de ingreso",
                            color = LogiValGreenDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Completa la información para enviar la solicitud a revisión.",
                            color = LogiValText.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        RequestTextField(
                            value = pavilion,
                            onValueChange = {
                                pavilion = it
                                message = ""
                            },
                            label = "Pabellón o zona",
                            placeholder = "Ej. Pabellón A / Zona de descarga",
                            icon = Icons.Default.Place,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        RequestTextField(
                            value = reason,
                            onValueChange = {
                                reason = it
                                message = ""
                            },
                            label = "Motivo de ingreso",
                            placeholder = "Carga, descarga, abastecimiento...",
                            icon = Icons.Default.Subject,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        RequestTextField(
                            value = productType,
                            onValueChange = {
                                productType = it
                                message = ""
                            },
                            label = "Tipo de producto",
                            placeholder = "Frutas, verduras, abarrotes...",
                            icon = Icons.Default.Inventory,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        RequestTextField(
                            value = estimatedWeight,
                            onValueChange = {
                                estimatedWeight = it
                                message = ""
                            },
                            label = "Peso estimado",
                            placeholder = "Ej. 500 kg",
                            icon = Icons.Default.Scale,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
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
                                    val vehicle = selectedVehicle

                                    if (vehicle == null) {
                                        message = "Primero selecciona un vehículo."
                                        loading = false
                                        return@launch
                                    }

                                    val merchantUid: String
                                    val merchantName: String

                                    if (isComerciante) {
                                        merchantUid = currentUser?.uid.orEmpty()
                                        merchantName = currentUser?.name.orEmpty()
                                    } else {
                                        val merchant = selectedMerchant

                                        if (merchant == null) {
                                            message = "Selecciona el comerciante asociado."
                                            loading = false
                                            return@launch
                                        }

                                        merchantUid = merchant.uid
                                        merchantName = merchant.name.ifBlank { merchant.email }
                                    }

                                    requestRepository.createRequest(
                                        uid = uid,
                                        vehicle = vehicle,
                                        pavilionId = pavilion,
                                        reason = reason,
                                        productType = productType,
                                        estimatedWeight = estimatedWeight,
                                        merchantUid = merchantUid,
                                        merchantName = merchantName
                                    ).onSuccess {
                                        navController.popBackStack()
                                    }.onFailure {
                                        message = it.message ?: "No se pudo crear la solicitud."
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
                                    contentDescription = "Enviar solicitud",
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Text(
                                    text = "Enviar solicitud",
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
private fun RequestFormHeader() {
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
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Solicitud",
                            tint = LogiValWhite,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Solicitar ingreso",
                        color = LogiValWhite,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Registra una nueva operación de acceso vehicular al mercado.",
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
private fun SelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = LogiValWhite),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValGreen.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = LogiValGreen.copy(alpha = 0.10f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = LogiValGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = LogiValGreenDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = subtitle,
                        color = LogiValText.copy(alpha = 0.62f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun VehicleChoiceChip(
    vehicle: Vehicle,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) LogiValGreen else LogiValText.copy(alpha = 0.50f)

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) LogiValGreen.copy(alpha = 0.12f) else LogiValWhite,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) LogiValGreen.copy(alpha = 0.35f) else LogiValText.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Column {
                Text(
                    text = vehicle.plate.ifBlank { "Sin placa" },
                    color = if (selected) LogiValGreenDark else LogiValText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = vehicle.type.ifBlank { "Vehículo" },
                    color = LogiValText.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun MerchantChoiceChip(
    merchant: AppUser,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) LogiValGreen.copy(alpha = 0.12f) else LogiValWhite,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) LogiValGreen.copy(alpha = 0.35f) else LogiValText.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = LogiValGreen.copy(alpha = if (selected) 0.18f else 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getInitials(merchant.name.ifBlank { merchant.email }),
                        color = LogiValGreenDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Column {
                Text(
                    text = merchant.name.ifBlank { merchant.email },
                    color = if (selected) LogiValGreenDark else LogiValText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Comerciante",
                    color = LogiValText.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun CurrentMerchantCard(
    name: String,
    email: String
) {
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
                color = LogiValGreen.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getInitials(name),
                        color = LogiValGreenDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = name.ifBlank { "Comerciante actual" },
                    color = LogiValGreenDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                if (email.isNotBlank()) {
                    Text(
                        text = email,
                        color = LogiValText.copy(alpha = 0.62f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
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

@Composable
private fun WarningText(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = LogiValRed.copy(alpha = 0.08f),
        border = BorderStroke(
            width = 1.dp,
            color = LogiValRed.copy(alpha = 0.12f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = LogiValRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
    }
}

private fun getInitials(value: String): String {
    val parts = value
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "LV"
    }
}