package com.example.sicuan.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun SiCuanLoadingState(
    message: String = "Memuat data...",
    modifier: Modifier = Modifier
) {
    SiCuanCard(
        modifier = modifier,
        contentPadding = PaddingValues(SiCuanDimens.SpacingLg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SiCuanEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    SiCuanCard(
        modifier = modifier,
        contentPadding = PaddingValues(SiCuanDimens.SpacingLg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!actionText.isNullOrBlank() && onActionClick != null) {
                SiCuanSecondaryButton(
                    text = actionText,
                    onClick = onActionClick
                )
            }
        }
    }
}

@Composable
fun SiCuanErrorState(
    title: String = "Terjadi Kesalahan",
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    SiCuanCard(
        modifier = modifier,
        contentPadding = PaddingValues(SiCuanDimens.SpacingLg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!actionText.isNullOrBlank() && onActionClick != null) {
                SiCuanSecondaryButton(
                    text = actionText,
                    onClick = onActionClick
                )
            }
        }
    }
}
