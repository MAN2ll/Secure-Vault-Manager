package com.securevault.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SecureVaultNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Минимальная заглушка вместо полноценной навигации
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔐 Secure Vault", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Минимальная сборка для теста", style = MaterialTheme.typography.bodyMedium)
    }
}
