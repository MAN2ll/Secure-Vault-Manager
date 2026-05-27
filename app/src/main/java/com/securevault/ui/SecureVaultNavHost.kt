package com.securevault.ui

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.securevault.ui.screens.*

sealed class Screen(val route: String) {
    object Lock : Screen("lock")
    object VaultList : Screen("vault_list")
    object EntryEdit : Screen("entry_edit?id={id}") {
        fun withId(id: Long?) = if (id != null) "entry_edit?id=$id" else "entry_edit"
    }
}

@Composable
fun SecureVaultNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Lock.route
    ) {
        composable(Screen.Lock.route) {
            LockScreen(
                onUnlocked = {
                    navController.navigate(Screen.VaultList.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                },
                onBiometricRequest = {
                    // Биометрия вызывается из Activity — здесь просто переходим
                    // В реальной реализации используйте BiometricHelper из Activity
                }
            )
        }

        composable(Screen.VaultList.route) {
            VaultListScreen(
                onAddEntry = { navController.navigate(Screen.EntryEdit.withId(null)) },
                onEditEntry = { id -> navController.navigate(Screen.EntryEdit.withId(id)) },
                onLock = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.VaultList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EntryEdit.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val rawId = backStackEntry.arguments?.getLong("id") ?: -1L
            val entryId = if (rawId == -1L) null else rawId
            EntryEditScreen(
                entryId = entryId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
