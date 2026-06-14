package com.example.sicuan.presentation.screen.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sicuan.R
import com.example.sicuan.presentation.viewmodel.FirebaseAuthUiState
import com.example.sicuan.presentation.viewmodel.ThemeViewModel
import androidx.compose.ui.platform.LocalContext
import android.content.SharedPreferences

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    firebaseAuthUiState: FirebaseAuthUiState,
    themeViewModel: ThemeViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onExportReport: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    val darkBackground = MaterialTheme.colorScheme.background
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val iconColor = MaterialTheme.colorScheme.primary

    val context = LocalContext.current
    val prefs: SharedPreferences = remember { context.getSharedPreferences("sicuan_user_prefs", android.content.Context.MODE_PRIVATE) }

    var isSmartNotifEnabled by remember { mutableStateOf(prefs.getBoolean("smart_notif_enabled", true)) }
    var isBiometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric_enabled", false)) }
    val isDarkModeEnabled by themeViewModel.isDarkMode.collectAsState()

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Profil Kamu", color = primaryText, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryText)
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // To balance center title
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Picture & Name
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = firebaseAuthUiState.photoUrl ?: R.drawable.ic_launcher_background,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .clickable { onNavigateToEditProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = firebaseAuthUiState.username?.takeIf { it.isNotBlank() } ?: firebaseAuthUiState.displayName ?: "Pengguna",
                color = primaryText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(cardBackground, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aktif & Cuaners", color = iconColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AKUN SAYA
            ProfileSectionLabel("AKUN SAYA")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        text = "Edit Profil",
                        onClick = onNavigateToEditProfile
                    )
                    Divider(color = darkBackground, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        text = "Ubah Kata Sandi",
                        onClick = onNavigateToChangePassword
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FITUR PINTAR
            ProfileSectionLabel("FITUR PINTAR")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuToggle(
                        icon = Icons.Default.NotificationsActive,
                        text = "Smart Notifications & Nudges",
                        isChecked = isSmartNotifEnabled,
                        onCheckedChange = {
                            isSmartNotifEnabled = it
                            prefs.edit().putBoolean("smart_notif_enabled", it).apply()
                            if (it) {
                                // Aktifkan ulang worker
                                val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.sicuan.worker.ReminderWorker>(6, java.util.concurrent.TimeUnit.HOURS).build()
                                androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                    "SmartReminderWorker",
                                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                                    workRequest
                                )
                            } else {
                                // Matikan worker
                                androidx.work.WorkManager.getInstance(context).cancelUniqueWork("SmartReminderWorker")
                            }
                        }
                    )
                    Divider(color = darkBackground, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuToggle(
                        icon = Icons.Default.Fingerprint,
                        text = "Biometric Security",
                        isChecked = isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                // Cek apakah perangkat support biometric
                                val biometricManager = androidx.biometric.BiometricManager.from(context)
                                val canAuth = biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)
                                if (canAuth == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
                                    isBiometricEnabled = true
                                    prefs.edit().putBoolean("biometric_enabled", true).apply()
                                } else {
                                    // Perangkat tidak support
                                    isBiometricEnabled = false
                                }
                            } else {
                                isBiometricEnabled = false
                                prefs.edit().putBoolean("biometric_enabled", false).apply()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // KEUANGAN
            ProfileSectionLabel("KEUANGAN")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Description,
                    text = "Ekspor Laporan",
                    onClick = onExportReport
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LAINNYA
            ProfileSectionLabel("LAINNYA")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuAnimatedToggle(
                        icon = if (isDarkModeEnabled) Icons.Default.DarkMode else Icons.Default.LightMode,
                        textMode = isDarkModeEnabled,
                        textDark = "Dark Mode",
                        textLight = "Light Mode",
                        isChecked = isDarkModeEnabled,
                        onCheckedChange = { themeViewModel.toggleTheme() },
                        primaryText = primaryText,
                        progressColor = iconColor
                    )
                    Divider(color = darkBackground, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        text = "Keluar",
                        textColor = MaterialTheme.colorScheme.error,
                        iconColor = MaterialTheme.colorScheme.error,
                        onClick = onSignOut
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileSectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    iconColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = textColor, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ProfileMenuToggle(
    icon: ImageVector,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileMenuAnimatedToggle(
    icon: ImageVector,
    textMode: Boolean,
    textDark: String,
    textLight: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryText: Color,
    progressColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = icon,
            transitionSpec = {
                (fadeIn() + slideInVertically { height -> height }).togetherWith(fadeOut() + slideOutVertically { height -> -height })
            },
            label = "IconAnimation"
        ) { targetIcon ->
            Icon(targetIcon, contentDescription = null, tint = primaryText, modifier = Modifier.size(24.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        AnimatedContent(
            targetState = textMode,
            transitionSpec = {
                (fadeIn() + slideInVertically { height -> height }).togetherWith(fadeOut() + slideOutVertically { height -> -height })
            },
            modifier = Modifier.weight(1f),
            label = "TextAnimation"
        ) { isDark ->
            Text(if (isDark) textDark else textLight, color = primaryText, fontSize = 16.sp)
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = progressColor,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}
