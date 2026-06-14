package com.example.sicuan.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sicuan.ui.theme.SiCuanDimens

import androidx.compose.material.icons.outlined.School

@Composable
fun SiCuanActionButtons(
    onInputClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onAiClick: () -> Unit,
    onEdukasiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SiCuanDimens.SpacingLg),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButtonItem(icon = Icons.Outlined.Edit, label = "Input", onClick = onInputClick)
        ActionButtonItem(icon = Icons.Outlined.Calculate, label = "Kalkulator", onClick = onCalculatorClick)
        ActionButtonItem(icon = Icons.Outlined.AutoAwesome, label = "Tanya AI", onClick = onAiClick)
        ActionButtonItem(icon = Icons.Outlined.School, label = "Edukasi", onClick = onEdukasiClick)
    }
}

@Composable
private fun ActionButtonItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
