package com.securevault.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.securevault.ui.screens.LockScreen
import com.securevault.ui.screens.VaultListScreen

@Composable
fun SecureVaultApp() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "lock") {
        composable("lock") {
            LockScreen(
                onUnlock = { navController.navigate("vault") },
                onSetup = { navController.navigate("vault") }
            )
        }
        composable("vault") {
            VaultListScreen(
                onBack = { navController.popBackStack() },
                onEntryClick = { /* TODO */ }
            )
        }
    }
}
