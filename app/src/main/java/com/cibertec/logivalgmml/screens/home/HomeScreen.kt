package com.cibertec.logivalgmml.screens.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText
import com.cibertec.logivalgmml.ui.theme.LogiValWhite

private data class BottomTab(
    val title: String,
    val icon: ImageVector,
    val type: MainTabType
)

private enum class MainTabType {
    INICIO,
    ESCANEAR,
    HISTORIAL,
    DASHBOARD,
    INCIDENCIAS,
    PERFIL
}

@Composable
fun HomeScreen(navController: NavController) {
    val authRepository = remember { AuthRepository() }

    var user by remember { mutableStateOf<AppUser?>(null) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        user = authRepository.getCurrentUserProfile()
    }

    val role = user?.role.orEmpty()

    val tabs = remember(role) {
        when {
            role.isAdminForHome() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Dashboard", Icons.Default.Dashboard, MainTabType.DASHBOARD),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            role.isControlForHome() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Escanear", Icons.Default.QrCodeScanner, MainTabType.ESCANEAR),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            role.isTransportistaForHome() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Incidencias", Icons.Default.ReportProblem, MainTabType.INCIDENCIAS),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            role.isComercianteForHome() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Incidencias", Icons.Default.ReportProblem, MainTabType.INCIDENCIAS),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            else -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )
        }
    }

    if (selectedIndex > tabs.lastIndex) {
        selectedIndex = 0
    }

    Scaffold(
        containerColor = LogiValBackground,
        bottomBar = {
            NavigationBar(
                containerColor = LogiValWhite,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedIndex == index) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LogiValGreen,
                            selectedTextColor = LogiValGreen,
                            indicatorColor = LogiValGreen.copy(alpha = 0.12f),
                            unselectedIconColor = LogiValText.copy(alpha = 0.52f),
                            unselectedTextColor = LogiValText.copy(alpha = 0.52f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        val selectedTab = tabs.getOrNull(selectedIndex)?.type ?: MainTabType.INICIO

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LogiValBackground)
        ) {
            when (selectedTab) {
                MainTabType.INICIO -> {
                    InicioTabContent(
                        user = user,
                        navController = navController
                    )
                }

                MainTabType.ESCANEAR -> {
                    ScannerTabContent(navController = navController)
                }

                MainTabType.HISTORIAL -> {
                    HistorialTabContent(
                        role = role,
                        navController = navController
                    )
                }

                MainTabType.DASHBOARD -> {
                    DashboardTabContent(navController = navController)
                }

                MainTabType.INCIDENCIAS -> {
                    IncidenciasTabContent(navController = navController)
                }

                MainTabType.PERFIL -> {
                    PerfilTabContent(
                        user = user,
                        authRepository = authRepository,
                        navController = navController
                    )
                }
            }
        }
    }

}

@Composable
private fun InicioTabContent(
    user: AppUser?,
    navController: NavController
) {
    val role = user?.role.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "LogiVal GMML",
            color = LogiValGreenDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hola, ${user?.name ?: "usuario"}",
            color = LogiValGreenDark,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Rol: ${role.toRoleLabelForHome()}",
            color = LogiValText.copy(alpha = 0.70f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            role.isTransportistaForHome() -> TransportistaHomeCards(navController)
            role.isComercianteForHome() -> ComercianteHomeCards(navController)
            role.isControlForHome() -> ControlHomeCards(navController)
            role.isAdminForHome() -> AdminHomeCards(navController)
            else -> DefaultHomeCards(navController)
        }
    }

}

@Composable
private fun TransportistaHomeCards(navController: NavController) {
    QuickCard(
        title = "Mis vehículos",
        subtitle = "Registrar y consultar vehículos",
        icon = Icons.Default.DirectionsCar
    ) {
        navController.navigate(Routes.VEHICLE_LIST)
    }

    QuickCard(
        title = "Nueva solicitud",
        subtitle = "Solicitar ingreso al mercado",
        icon = Icons.Default.Assignment
    ) {
        navController.navigate(Routes.REQUEST_FORM)
    }

    QuickCard(
        title = "Mis solicitudes",
        subtitle = "Consultar estado, aprobación y QR",
        icon = Icons.Default.History
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "QR activo",
        subtitle = "Ver solicitudes aprobadas con QR",
        icon = Icons.Default.QrCode
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Reportar incidencia",
        subtitle = "Registrar problemas durante el acceso",
        icon = Icons.Default.ReportProblem,
        danger = true
    ) {
        navController.navigate(Routes.INCIDENT_FORM)
    }

}

@Composable
private fun ComercianteHomeCards(navController: NavController) {
    QuickCard(
        title = "Solicitudes asociadas",
        subtitle = "Consultar accesos vinculados a carga o descarga",
        icon = Icons.Default.Assignment
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Nueva solicitud",
        subtitle = "Solicitar ingreso para operación comercial",
        icon = Icons.Default.Store
    ) {
        navController.navigate(Routes.REQUEST_FORM)
    }

    QuickCard(
        title = "Estado de accesos",
        subtitle = "Revisar solicitudes pendientes y aprobadas",
        icon = Icons.Default.Verified
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Reportar incidencia",
        subtitle = "Informar problemas en carga, descarga o acceso",
        icon = Icons.Default.ReportProblem,
        danger = true
    ) {
        navController.navigate(Routes.INCIDENT_FORM)
    }

}

@Composable
private fun ControlHomeCards(navController: NavController) {
    QuickCard(
        title = "Escanear QR",
        subtitle = "Validar ingreso y salida de vehículos",
        icon = Icons.Default.QrCodeScanner
    ) {
        navController.navigate(Routes.QR_SCANNER)
    }

    QuickCard(
        title = "Solicitudes pendientes",
        subtitle = "Revisar, aprobar o rechazar accesos",
        icon = Icons.Default.Assignment
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Vehículos dentro",
        subtitle = "Consultar unidades actualmente en mercado",
        icon = Icons.Default.DirectionsCar
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Registrar incidencia",
        subtitle = "Reportar problemas operativos del control",
        icon = Icons.Default.ReportProblem,
        danger = true
    ) {
        navController.navigate(Routes.INCIDENT_FORM)
    }

    QuickCard(
        title = "Dashboard operativo",
        subtitle = "Ver ingresos, salidas e incidencias",
        icon = Icons.Default.Dashboard
    ) {
        navController.navigate(Routes.DASHBOARD)
    }

}

@Composable
private fun AdminHomeCards(navController: NavController) {
    QuickCard(
        title = "Dashboard administrativo",
        subtitle = "Ver indicadores completos del sistema",
        icon = Icons.Default.Dashboard
    ) {
        navController.navigate(Routes.DASHBOARD)
    }

    QuickCard(
        title = "Solicitudes",
        subtitle = "Aprobar, rechazar y revisar accesos",
        icon = Icons.Default.Assignment
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Vehículos activos",
        subtitle = "Consultar vehículos registrados",
        icon = Icons.Default.DirectionsCar
    ) {
        navController.navigate(Routes.VEHICLE_LIST)
    }

    QuickCard(
        title = "Incidencias",
        subtitle = "Revisar problemas registrados",
        icon = Icons.Default.ReportProblem,
        danger = true
    ) {
        navController.navigate(Routes.INCIDENT_LIST)
    }

}

@Composable
private fun DefaultHomeCards(navController: NavController) {
    QuickCard(
        title = "Solicitudes",
        subtitle = "Consultar operaciones registradas",
        icon = Icons.Default.Assignment
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

    QuickCard(
        title = "Perfil",
        subtitle = "Ver información del usuario",
        icon = Icons.Default.Person
    ) {
        navController.navigate(Routes.PROFILE)
    }

}

@Composable
private fun ScannerTabContent(navController: NavController) {
    SimpleTabLayout(
        title = "Validar QR",
        subtitle = "Registra ingreso y salida de vehículos desde el punto de control.",
        buttonText = "Abrir escáner",
        buttonColor = LogiValGreen
    ) {
        navController.navigate(Routes.QR_SCANNER)
    }
}

@Composable
private fun HistorialTabContent(
    role: String,
    navController: NavController
) {
    val title = when {
        role.isControlForHome() -> "Historial operativo"
        role.isAdminForHome() -> "Historial general"
        role.isComercianteForHome() -> "Solicitudes asociadas"
        else -> "Mis solicitudes"
    }

    val subtitle = when {
        role.isControlForHome() -> "Consulta solicitudes pendientes, aprobadas, rechazadas y finalizadas."
        role.isAdminForHome() -> "Revisa todas las solicitudes registradas en el sistema."
        role.isComercianteForHome() -> "Consulta accesos vinculados a tus operaciones comerciales."
        else -> "Consulta el estado de tus solicitudes y revisa tu QR cuando corresponda."
    }

    SimpleTabLayout(
        title = title,
        subtitle = subtitle,
        buttonText = "Ver solicitudes",
        buttonColor = LogiValGreen
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }

}

@Composable
private fun DashboardTabContent(navController: NavController) {
    SimpleTabLayout(
        title = "Dashboard administrativo",
        subtitle = "Monitorea solicitudes, vehículos dentro, ingresos, salidas e incidencias.",
        buttonText = "Abrir dashboard",
        buttonColor = LogiValGreen
    ) {
        navController.navigate(Routes.DASHBOARD)
    }
}

@Composable
private fun IncidenciasTabContent(navController: NavController) {
    SimpleTabLayout(
        title = "Incidencias",
        subtitle = "Reporta y consulta problemas operativos del mercado.",
        buttonText = "Ver incidencias",
        buttonColor = LogiValRed
    ) {
        navController.navigate(Routes.INCIDENT_LIST)
    }
}

@Composable
private fun PerfilTabContent(
    user: AppUser?,
    authRepository: AuthRepository,
    navController: NavController
) {
    val role = user?.role.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Perfil",
            color = LogiValGreenDark,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = LogiValWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = user?.name ?: "Usuario",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LogiValGreenDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Correo: ${user?.email ?: "-"}",
                    color = LogiValText.copy(alpha = 0.75f)
                )

                Text(
                    text = "Teléfono: ${user?.phone ?: "-"}",
                    color = LogiValText.copy(alpha = 0.75f)
                )

                Text(
                    text = "Rol: ${role.toRoleLabelForHome()}",
                    color = LogiValGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Estado: ${user?.status ?: "-"}",
                    color = LogiValText.copy(alpha = 0.75f)
                )
            }
        }

        Button(
            onClick = {
                authRepository.logout()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LogiValRed,
                contentColor = LogiValWhite
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun SimpleTabLayout(
    title: String,
    subtitle: String,
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = LogiValGreenDark,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            color = LogiValText.copy(alpha = 0.70f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = LogiValWhite
            )
        ) {
            Text(
                text = buttonText,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun QuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    val mainColor = if (danger) LogiValRed else LogiValGreen
    val titleColor = if (danger) LogiValRed else LogiValGreenDark

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = mainColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(16.dp),
                color = mainColor.copy(alpha = 0.12f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = mainColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = LogiValText.copy(alpha = 0.68f),
                    fontSize = 14.sp
                )
            }
        }
    }

}

private fun String.normalizedRoleForHome(): String {
    return this.trim()
        .uppercase()
        .replace(" ", "_")
}

private fun String.isTransportistaForHome(): Boolean {
    return normalizedRoleForHome() == "TRANSPORTISTA"
}

private fun String.isComercianteForHome(): Boolean {
    return normalizedRoleForHome() == "COMERCIANTE"
}

private fun String.isControlForHome(): Boolean {
    val role = normalizedRoleForHome()

    return role == "CONTROL" ||
            role == "PERSONAL_DE_CONTROL"

}

private fun String.isAdminForHome(): Boolean {
    val role = normalizedRoleForHome()

    return role == "ADMIN" ||
            role == "ADMINISTRADOR"

}

private fun String.toRoleLabelForHome(): String {
    return when (normalizedRoleForHome()) {
        "TRANSPORTISTA" -> "Transportista"
        "COMERCIANTE" -> "Comerciante"
        "CONTROL", "PERSONAL_DE_CONTROL" -> "Personal de control"
        "ADMIN", "ADMINISTRADOR" -> "Administrador"
        else -> "Cargando..."
    }
}
