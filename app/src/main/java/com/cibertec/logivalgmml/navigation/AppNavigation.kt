package com.cibertec.logivalgmml.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cibertec.logivalgmml.screens.auth.LoginScreen
import com.cibertec.logivalgmml.screens.auth.RegisterScreen
import com.cibertec.logivalgmml.screens.dashboard.DashboardScreen
import com.cibertec.logivalgmml.screens.home.HomeScreen
import com.cibertec.logivalgmml.screens.incidents.IncidentFormScreen
import com.cibertec.logivalgmml.screens.incidents.IncidentListScreen
import com.cibertec.logivalgmml.screens.onboarding.OnboardingScreen
import com.cibertec.logivalgmml.screens.profile.ProfileScreen
import com.cibertec.logivalgmml.screens.qr.QrAccessScreen
import com.cibertec.logivalgmml.screens.qr.QrScannerScreen
import com.cibertec.logivalgmml.screens.requests.RequestDetailScreen
import com.cibertec.logivalgmml.screens.requests.RequestFormScreen
import com.cibertec.logivalgmml.screens.requests.RequestListScreen
import com.cibertec.logivalgmml.screens.splash.SplashScreen
import com.cibertec.logivalgmml.screens.vehicles.VehicleFormScreen
import com.cibertec.logivalgmml.screens.vehicles.VehicleListScreen
import com.cibertec.logivalgmml.screens.auth.ForgotPasswordScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(Routes.VEHICLE_LIST) {
            VehicleListScreen(navController = navController)
        }

        composable(Routes.VEHICLE_FORM) {
            VehicleFormScreen(navController = navController)
        }

        composable(Routes.REQUEST_LIST) {
            RequestListScreen(navController = navController)
        }

        composable(Routes.REQUEST_FORM) {
            RequestFormScreen(navController = navController)
        }

        composable(
            route = Routes.REQUEST_DETAIL_ROUTE,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            RequestDetailScreen(
                navController = navController,
                requestId = backStackEntry.arguments?.getString("requestId").orEmpty()
            )
        }

        composable(
            route = Routes.QR_ACCESS_ROUTE,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            QrAccessScreen(
                navController = navController,
                requestId = backStackEntry.arguments?.getString("requestId").orEmpty()
            )
        }

        composable(Routes.QR_SCANNER) {
            QrScannerScreen(navController = navController)
        }

        composable(Routes.INCIDENT_LIST) {
            IncidentListScreen(navController = navController)
        }

        composable(Routes.INCIDENT_FORM) {
            IncidentFormScreen(navController = navController)
        }
    }
}