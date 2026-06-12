package com.example.sicuan.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanLoadingState
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.viewmodel.FirebaseAuthUiState
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun ProfileScreen(
    firebaseAuthUiState: FirebaseAuthUiState,
    onRetrySignIn: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        when {
            firebaseAuthUiState.isLoading -> {
                SiCuanLoadingState(
                    message = "Menghubungkan ke Firebase..."
                )
            }

            firebaseAuthUiState.errorMessage != null -> {
                SiCuanCard {
                    Text(
                        text = "Firebase belum terhubung",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = firebaseAuthUiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SiCuanPrimaryButton(
                        text = "Coba Login Ulang",
                        onClick = onRetrySignIn
                    )
                }
            }

            firebaseAuthUiState.isSignedIn -> {
                SiCuanCard {
                    Text(
                        text = "Firebase Backend Connected",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Authentication: Anonymous",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "UID:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = firebaseAuthUiState.uid,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Status ini menandakan aplikasi sudah berhasil membuat user anonymous di Firebase Authentication.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }
}