package com.example.sicuan.presentation.screen.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.ui.theme.SiCuanDimens
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorBottomSheet(
    rates: List<CurrencyRate>,
    onDismissRequest: () -> Unit,
    onUseResult: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        CalculatorContent(
            rates = rates,
            onUseResult = {
                onUseResult(it)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun CalculatorContent(
    rates: List<CurrencyRate>,
    onUseResult: (String) -> Unit
) {
    var displayValue by remember { mutableStateOf("0") }
    var storedValue by remember { mutableStateOf<Double?>(null) }
    var currentOperation by remember { mutableStateOf<Operation?>(null) }
    var isNewInput by remember { mutableStateOf(true) }

    var isConverterMode by remember { mutableStateOf(false) }
    var baseCurrency by remember { mutableStateOf("IDR") }
    var targetCurrency by remember { mutableStateOf("USD") }

    val allCurrencies = remember(rates) {
        val set = mutableSetOf("IDR")
        rates.forEach { set.add(it.base); set.add(it.quote) }
        set.toList().sorted()
    }

    val convertedValue = remember(displayValue, baseCurrency, targetCurrency, rates) {
        val value = displayValue.toDoubleOrNull() ?: 0.0
        if (baseCurrency == targetCurrency) return@remember value
        
        // Find rate base -> target
        val directRate = rates.find { it.base == baseCurrency && it.quote == targetCurrency }?.rate
        if (directRate != null) return@remember value * directRate

        val reverseRate = rates.find { it.base == targetCurrency && it.quote == baseCurrency }?.rate
        if (reverseRate != null) return@remember value / reverseRate

        // Through IDR
        val toIdr = if (baseCurrency == "IDR") 1.0 else {
            rates.find { it.quote == "IDR" && it.base == baseCurrency }?.rate 
            ?: (1.0 / (rates.find { it.base == "IDR" && it.quote == baseCurrency }?.rate ?: 1.0))
        }

        val fromIdrToTarget = if (targetCurrency == "IDR") 1.0 else {
            rates.find { it.base == "IDR" && it.quote == targetCurrency }?.rate
            ?: (1.0 / (rates.find { it.quote == "IDR" && it.base == targetCurrency }?.rate ?: 1.0))
        }

        value * toIdr * fromIdrToTarget
    }

    fun handleInput(input: String) {
        when (input) {
            "C" -> {
                displayValue = "0"
                storedValue = null
                currentOperation = null
                isNewInput = true
            }
            "⌫" -> {
                if (displayValue.length > 1) {
                    displayValue = displayValue.dropLast(1)
                } else {
                    displayValue = "0"
                    isNewInput = true
                }
            }
            "÷", "×", "-", "+" -> {
                val currentNum = displayValue.toDoubleOrNull() ?: 0.0
                if (storedValue == null) {
                    storedValue = currentNum
                } else if (!isNewInput) {
                    val result = calculateResult(storedValue!!, currentNum, currentOperation)
                    storedValue = result
                    displayValue = formatResult(result)
                }
                currentOperation = Operation.fromSymbol(input)
                isNewInput = true
            }
            "=" -> {
                val currentNum = displayValue.toDoubleOrNull() ?: 0.0
                if (storedValue != null && currentOperation != null) {
                    val result = calculateResult(storedValue!!, currentNum, currentOperation)
                    displayValue = formatResult(result)
                    storedValue = null
                    currentOperation = null
                    isNewInput = true
                }
            }
            "." -> {
                if (!displayValue.contains(".")) {
                    displayValue += "."
                    isNewInput = false
                }
            }
            "000" -> {
                if (!isNewInput && displayValue != "0") {
                    displayValue += "000"
                }
            }
            "%" -> {
                val num = displayValue.toDoubleOrNull() ?: 0.0
                displayValue = formatResult(num / 100)
            }
            else -> { // Numbers
                if (isNewInput) {
                    displayValue = input
                    isNewInput = false
                } else {
                    displayValue = if (displayValue == "0") input else displayValue + input
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SiCuanDimens.SpacingLg)
    ) {
        // Mode Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = !isConverterMode,
                onClick = { isConverterMode = false },
                label = { Text("Kalkulator") }
            )
            Spacer(modifier = Modifier.width(SiCuanDimens.SpacingMd))
            FilterChip(
                selected = isConverterMode,
                onClick = { isConverterMode = true },
                label = { Text("Mata Uang") }
            )
        }

        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))

        // Display Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(SiCuanDimens.SpacingLg),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                if (isConverterMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrencySelector(
                            selectedCurrency = baseCurrency,
                            currencies = allCurrencies,
                            onSelect = { baseCurrency = it }
                        )
                        Text(
                            text = displayValue,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                val temp = baseCurrency
                                baseCurrency = targetCurrency
                                targetCurrency = temp
                            }
                    )
                    Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrencySelector(
                            selectedCurrency = targetCurrency,
                            currencies = allCurrencies,
                            onSelect = { targetCurrency = it }
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f", convertedValue).replace(".00", ""),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

        // Keypad
        val buttons = listOf(
            "C", "⌫", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "000", "="
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            items(buttons) { btn ->
                CalculatorButton(
                    text = btn,
                    onClick = { handleInput(btn) },
                    isAction = btn in listOf("C", "⌫", "%"),
                    isOperator = btn in listOf("÷", "×", "-", "+", "=")
                )
            }
        }

        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

        SiCuanPrimaryButton(
            text = "Gunakan Hasil di Nominal",
            onClick = {
                val resultToUse = if (isConverterMode) convertedValue.toString() else displayValue
                onUseResult(formatResult(resultToUse.toDoubleOrNull() ?: 0.0))
            }
        )
        
        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))
    }
}

@Composable
private fun CurrencySelector(
    selectedCurrency: String,
    currencies: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedCurrency)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelect(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    isAction: Boolean,
    isOperator: Boolean
) {
    val backgroundColor = when {
        isOperator -> MaterialTheme.colorScheme.primary
        isAction -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = when {
        isOperator -> MaterialTheme.colorScheme.onPrimary
        isAction -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = textColor
        )
    }
}

private enum class Operation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE;

    companion object {
        fun fromSymbol(symbol: String): Operation? = when (symbol) {
            "+" -> ADD
            "-" -> SUBTRACT
            "×" -> MULTIPLY
            "÷" -> DIVIDE
            else -> null
        }
    }
}

private fun calculateResult(a: Double, b: Double, op: Operation?): Double {
    return when (op) {
        Operation.ADD -> a + b
        Operation.SUBTRACT -> a - b
        Operation.MULTIPLY -> a * b
        Operation.DIVIDE -> if (b != 0.0) a / b else 0.0
        null -> b
    }
}

private fun formatResult(value: Double): String {
    val longVal = value.toLong()
    return if (value == longVal.toDouble()) {
        longVal.toString()
    } else {
        String.format(Locale.US, "%.2f", value)
    }
}
