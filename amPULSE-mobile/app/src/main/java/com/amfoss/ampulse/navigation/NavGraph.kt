package com.amfoss.ampulse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amfoss.ampulse.ui.splash.SplashScreen
import com.amfoss.ampulse.ui.login.LoginScreen
import com.amfoss.ampulse.ui.permission.PermissionScreen
import com.amfoss.ampulse.ui.dashboard.DashboardScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Permission.route)
            })
        }
        composable(Screen.Permission.route) {
            PermissionScreen(onPermissionGranted = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
