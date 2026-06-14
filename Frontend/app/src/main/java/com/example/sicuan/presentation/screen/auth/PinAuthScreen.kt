package com.example.sicuan.presentation.screen.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sicuan.presentation.viewmodel.PinAuthViewModel
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun PinAuthScreen(
    viewModel: PinAuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }

    // Tampilkan BiometricPrompt otomatis saat layar dibuka
    LaunchedEffect(uiState.isBiometricEnabled) {
        if (uiState.isBiometricEnabled && !uiState.isSettingUpPin) {
            kotlinx.coroutines.delay(300) // Tunggu activity siap
            try {
                showBiometricPrompt(context as FragmentActivity, viewModel)
            } catch (_: Exception) {
                // Activity belum siap, user bisa tekan tombol manual
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(SiCuanDimens.SpacingLg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.isSettingUpPin) {
                    if (uiState.isConfirmingPin) "Konfirmasi PIN Anda" else "Buat PIN Baru"
                } else {
                    "Masukkan PIN Anda"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (uiState.isBiometricEnabled && !uiState.isSettingUpPin) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "atau gunakan sidik jari",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingXl))

            // PIN Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val isFilled = index < uiState.currentPinInput.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) MaterialTheme.colorScheme.onBackground
                                else Color.Transparent
                            )
                            .then(
                                if (!isFilled) Modifier.border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                                else Modifier
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))

            Text(
                text = uiState.errorMessage ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = if (uiState.errorMessage != null) MaterialTheme.colorScheme.error else Color.Transparent,
                modifier = Modifier.height(20.dp) // Fixed height to prevent layout shift
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Numpad
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingLg),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (row in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (col in 1..3) {
                        val number = row * 3 + col
                        NumpadButton(
                            text = number.toString(),
                            onClick = { viewModel.onNumberClick(number.toString()) }
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol biometric (jika aktif & bukan setup PIN baru)
                if (uiState.isBiometricEnabled && !uiState.isSettingUpPin) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                try {
                                    showBiometricPrompt(context as FragmentActivity, viewModel)
                                } catch (_: Exception) { }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(72.dp))
                }

                NumpadButton(
                    text = "0",
                    onClick = { viewModel.onNumberClick("0") }
                )

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = { viewModel.onBackspaceClick() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Hapus",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))
    }
}

private fun showBiometricPrompt(activity: FragmentActivity, viewModel: PinAuthViewModel) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.onBiometricSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // User cancelled or error — bisa input PIN manual
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Sidik jari tidak cocok — bisa coba lagi
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autentikasi SiCuan")
        .setSubtitle("Gunakan sidik jari untuk masuk")
        .setNegativeButtonText("Gunakan PIN")
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@Composable
private fun NumpadButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
