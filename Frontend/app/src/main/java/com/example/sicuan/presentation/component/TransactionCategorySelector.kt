package com.example.sicuan.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.TransactionCategory
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun TransactionCategorySelector(
    type: String,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseCategories = TransactionCategory.getCategoriesByType(type)

    val categories = if (
        selectedCategory.isNotBlank() &&
        selectedCategory !in baseCategories
    ) {
        listOf(selectedCategory) + baseCategories
    } else {
        baseCategories
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
    ) {
        Text(
            text = "Kategori",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(category)
                    },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    label = {
                        Text(text = category)
                    }
                )
            }
        }
    }
}
