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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.isAdminRole
import com.cibertec.logivalgmml.models.isControlRole
import com.cibertec.logivalgmml.models.roleLabel
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
            role.isAdminRole() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Dashboard", Icons.Default.Dashboard, MainTabType.DASHBOARD),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            role.isControlRole() -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Escanear", Icons.Default.QrCodeScanner, MainTabType.ESCANEAR),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )

            else -> listOf(
                BottomTab("Inicio", Icons.Default.Home, MainTabType.INICIO),
                BottomTab("Historial", Icons.Default.History, MainTabType.HISTORIAL),
                BottomTab("Incidencias", Icons.Default.ReportProblem, MainTabType.INCIDENCIAS),
                BottomTab("Perfil", Icons.Default.Person, MainTabType.PERFIL)
            )
        }
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
                            indicatorColor = LogiValGreen.copy(alpha = 0.10f),
                            unselectedIconColor = LogiValText.copy(alpha = 0.55f),
                            unselectedTextColor = LogiValText.copy(alpha = 0.55f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LogiValBackground)
        ) {
            val selectedTab = tabs.getOrNull(selectedIndex)?.type ?: MainTabType.INICIO

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
                    HistorialTabContent(navController = navController)
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

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Hola, ${user?.name ?: "usuario"}",
            color = LogiValGreenDark,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Rol: ${user?.role?.roleLabel() ?: "Cargando..."}",
            color = LogiValText.copy(alpha = 0.70f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            role.isAdminRole() -> {
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
                    icon = Icons.Default.History
                ) {
                    navController.navigate(Routes.REQUEST_LIST)
                }

                QuickCard(
                    title = "Incidencias",
                    subtitle = "Revisar problemas registrados",
                    icon = Icons.Default.ReportProblem
                ) {
                    navController.navigate(Routes.INCIDENT_LIST)
                }
            }

            role.isControlRole() -> {
                QuickCard(
                    title = "Escanear QR",
                    subtitle = "Validar ingreso y salida de vehículos",
                    icon = Icons.Default.QrCodeScanner
                ) {
                    navController.navigate(Routes.QR_SCANNER)
                }

                QuickCard(
                    title = "Solicitudes pendientes",
                    subtitle = "Revisar y aprobar accesos",
                    icon = Icons.Default.History
                ) {
                    navController.navigate(Routes.REQUEST_LIST)
                }

                QuickCard(
                    title = "Dashboard operativo",
                    subtitle = "Ver vehículos dentro e incidencias",
                    icon = Icons.Default.Dashboard
                ) {
                    navController.navigate(Routes.DASHBOARD)
                }
            }

            else -> {
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
                    icon = Icons.Default.History
                ) {
                    navController.navigate(Routes.REQUEST_FORM)
                }

                QuickCard(
                    title = "Mis solicitudes",
                    subtitle = "Consultar estado y QR",
                    icon = Icons.Default.History
                ) {
                    navController.navigate(Routes.REQUEST_LIST)
                }
            }
        }
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
private fun HistorialTabContent(navController: NavController) {
    SimpleTabLayout(
        title = "Historial de solicitudes",
        subtitle = "Consulta solicitudes pendientes, aprobadas, rechazadas y finalizadas.",
        buttonText = "Ver solicitudes",
        buttonColor = LogiValGreen
    ) {
        navController.navigate(Routes.REQUEST_LIST)
    }
}

@Composable
private fun DashboardTabContent(navController: NavController) {
    SimpleTabLayout(
        title = "Dashboard operativo",
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
                    text = "Rol: ${user?.role?.roleLabel() ?: "-"}",
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
    buttonColor: androidx.compose.ui.graphics.Color,
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
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            color = LogiValText.copy(alpha = 0.70f),
            fontSize = 15.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LogiValGreen.copy(alpha = 0.08f)
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
                color = LogiValGreen.copy(alpha = 0.12f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LogiValGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    color = LogiValGreenDark,
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