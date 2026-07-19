package com.amfoss.ampulse.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
}
