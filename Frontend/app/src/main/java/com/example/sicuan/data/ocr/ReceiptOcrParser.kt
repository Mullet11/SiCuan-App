package com.example.sicuan.data.ocr

import com.example.sicuan.domain.model.OcrReceiptResult
import java.text.SimpleDateFormat
import java.util.Locale

object ReceiptOcrParser {

    private val totalKeywords = listOf(
        "grand total",
        "total",
        "jumlah",
        "tagihan",
        "bayar",
        "tunai",
        "amount"
    )

    private val merchantIgnoredKeywords = listOf(
        "struk",
        "receipt",
        "nota",
        "invoice",
        "tanggal",
        "date",
        "total",
        "rp",
        "kasir",
        "qty",
        "jumlah",
        "harga",
        "subtotal",
        "bayar",
        "tunai",
        "kembali"
    )

    private val dateRegexPatterns = listOf(
        Regex("""\b\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\b"""),
        Regex("""\b\d{4}[/-]\d{1,2}[/-]\d{1,2}\b""")
    )

    fun parse(rawText: String): OcrReceiptResult {
        val lines = rawText
            .lines()
            .map { line ->
                line.trim()
            }
            .filter { line ->
                line.isNotBlank()
            }

        val merchant = findMerchant(lines)
        val amount = findBestAmount(lines)
        val parsedDate = findDate(lines)

        val amountText = amount?.toLong()?.toString().orEmpty()

        val title = if (merchant.isNotBlank()) {
            "Belanja di $merchant"
        } else {
            "Belanja dari OCR"
        }

        val note = buildString {
            append("Dibuat dari hasil scan OCR")

            if (parsedDate.dateText.isNotBlank()) {
                append(" • Tanggal struk: ${parsedDate.dateText}")
            }
        }

        return OcrReceiptResult(
            rawText = rawText,
            merchant = merchant,
            amountText = amountText,
            dateText = parsedDate.dateText,
            dateMillis = parsedDate.dateMillis,
            title = title,
            note = note
        )
    }

    private fun findMerchant(lines: List<String>): String {
        return lines.firstOrNull { line ->
            val lowerLine = line.lowercase()

            val isIgnoredLine = merchantIgnoredKeywords.any { keyword ->
                lowerLine.contains(keyword)
            }

            val hasLetter = line.any { char ->
                char.isLetter()
            }

            val hasEnoughLength = line.length in 3..40

            !isIgnoredLine && hasLetter && hasEnoughLength
        }.orEmpty()
    }

    private fun findBestAmount(lines: List<String>): Double? {
        val priorityLines = lines.filter { line ->
            val lowerLine = line.lowercase()

            totalKeywords.any { keyword ->
                lowerLine.contains(keyword)
            }
        }

        val priorityAmounts = priorityLines.flatMap { line ->
            extractAmountsFromLine(line)
        }

        if (priorityAmounts.isNotEmpty()) {
            return priorityAmounts.maxOrNull()
        }

        val nonDateLines = lines.filterNot { line ->
            isLikelyDateLine(line)
        }

        val allAmounts = nonDateLines.flatMap { line ->
            extractAmountsFromLine(line)
        }

        return allAmounts.maxOrNull()
    }

    private fun extractAmountsFromLine(line: String): List<Double> {
        val moneyRegex = Regex(
            pattern = """(?i)(rp\s*)?\d{1,3}([.,]\d{3})+([.,]\d{2})?|\d{4,}"""
        )

        return moneyRegex.findAll(line)
            .mapNotNull { matchResult ->
                parseMoney(matchResult.value)
            }
            .filter { amount ->
                amount >= 1000.0
            }
            .toList()
    }

    private fun parseMoney(value: String): Double? {
        var cleaned = value
            .uppercase()
            .replace("RP", "")
            .replace(" ", "")
            .trim()

        if (cleaned.contains(".") && cleaned.contains(",")) {
            cleaned = cleaned.substringBefore(",")
        }

        cleaned = cleaned.replace(Regex("[^0-9]"), "")

        return cleaned.toDoubleOrNull()
    }

    private fun findDate(lines: List<String>): ParsedDate {
        val joinedText = lines.joinToString(" ")

        for (regex in dateRegexPatterns) {
            val match = regex.find(joinedText)

            if (match != null) {
                val dateText = match.value
                val millis = parseDateToMillis(dateText)

                return ParsedDate(
                    dateText = dateText,
                    dateMillis = millis
                )
            }
        }

        return ParsedDate()
    }

    private fun isLikelyDateLine(line: String): Boolean {
        return dateRegexPatterns.any { regex ->
            regex.containsMatchIn(line)
        }
    }

    private fun parseDateToMillis(dateText: String): Long? {
        val formats = listOf(
            "dd/MM/yyyy",
            "d/M/yyyy",
            "dd-MM-yyyy",
            "d-M-yyyy",
            "dd/MM/yy",
            "d/M/yy",
            "dd-MM-yy",
            "d-M-yy",
            "yyyy/MM/dd",
            "yyyy/M/d",
            "yyyy-MM-dd",
            "yyyy-M-d"
        )

        for (format in formats) {
            try {
                val formatter = SimpleDateFormat(format, Locale("id", "ID"))
                formatter.isLenient = false

                val date = formatter.parse(dateText)

                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
                // Abaikan format yang tidak cocok.
            }
        }

        return null
    }

    private data class ParsedDate(
        val dateText: String = "",
        val dateMillis: Long? = null
    )
}