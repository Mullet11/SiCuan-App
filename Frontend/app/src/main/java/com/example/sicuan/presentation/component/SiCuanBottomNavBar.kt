package com.example.sicuan.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SiCuanBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        cutoutShape = CircleShape
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda", tint = if (currentRoute == "dashboard") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            label = { Text("Beranda", style = MaterialTheme.typography.labelSmall, color = if (currentRoute == "dashboard") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Outlined.PieChart, contentDescription = "Analisis", tint = if (currentRoute == "insight") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            label = { Text("Analisis", style = MaterialTheme.typography.labelSmall, color = if (currentRoute == "insight") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            selected = currentRoute == "insight",
            onClick = { onNavigate("insight") }
        )

        Spacer(Modifier.weight(1f))

        BottomNavigationItem(
            icon = { Icon(Icons.Outlined.Event, contentDescription = "Rencana", tint = if (currentRoute == "plan") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            label = { Text("Rencana", style = MaterialTheme.typography.labelSmall, color = if (currentRoute == "plan") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            selected = currentRoute == "plan",
            onClick = { onNavigate("plan") }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil", tint = if (currentRoute == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            label = { Text("Profil", style = MaterialTheme.typography.labelSmall, color = if (currentRoute == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") }
        )
    }
}
