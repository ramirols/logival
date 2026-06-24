package com.cibertec.logivalgmml.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cibertec.logivalgmml.models.AppUser
import com.cibertec.logivalgmml.models.roleLabel
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.ui.components.LogiValScaffold
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValRed
import com.cibertec.logivalgmml.ui.theme.LogiValText

@Composable
fun ProfileScreen(navController: NavController) {
    val authRepository = remember { AuthRepository() }
    var user by remember { mutableStateOf<AppUser?>(null) }

    LaunchedEffect(Unit) {
        user = authRepository.getCurrentUserProfile()
    }

    LogiValScaffold(
        title = "Perfil",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = user?.name ?: "Usuario",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = LogiValGreenDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Correo: ${user?.email ?: "-"}", color = LogiValText)
                    Text("Teléfono: ${user?.phone ?: "-"}", color = LogiValText)
                    Text("Rol: ${user?.role?.roleLabel() ?: "-"}", color = LogiValGreen)
                    Text("Estado: ${user?.status ?: "-"}", color = LogiValText)
                }
            }

            Button(
                onClick = {
                    authRepository.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogiValRed),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}