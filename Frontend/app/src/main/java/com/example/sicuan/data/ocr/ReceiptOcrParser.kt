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

        val category = guessCategory(merchant)

        return OcrReceiptResult(
            rawText = rawText,
            merchant = merchant,
            amountText = amountText,
            dateText = parsedDate.dateText,
            dateMillis = parsedDate.dateMillis,
            title = title,
            note = note,
            category = category
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
        val priorityAmounts = mutableListOf<Double>()

        for (i in lines.indices) {
            val line = lines[i]
            val lowerLine = line.lowercase()

            if (totalKeywords.any { keyword -> lowerLine.contains(keyword) }) {
                val amountsOnSameLine = extractAmountsFromLine(line, isPriority = true)
                priorityAmounts.addAll(amountsOnSameLine)

                if (amountsOnSameLine.isEmpty() && i + 1 < lines.size) {
                    priorityAmounts.addAll(extractAmountsFromLine(lines[i + 1], isPriority = true))
                }
            }
        }

        if (priorityAmounts.isNotEmpty()) {
            val validPriority = priorityAmounts.filter { it <= 20_000_000.0 }
            if (validPriority.isNotEmpty()) {
                val priorityMax = validPriority.maxOrNull()
                val allAmounts = lines.flatMap { extractAmountsFromLine(it, false) }.filter { it <= 20_000_000.0 }
                val mathTotal = findMathTotal(allAmounts)
                return mathTotal ?: priorityMax
            }
        }

        val ignoredAmountKeywords = listOf("cash", "change", "kembali", "tunai", "npwp", "tel", "phone", "wa", "whatsapp", "diskon", "discount")

        val validLines = lines.filterNot { line ->
            isLikelyDateLine(line) || ignoredAmountKeywords.any { line.lowercase().contains(it) }
        }

        val allAmounts = validLines.flatMap { line ->
            extractAmountsFromLine(line, isPriority = false)
        }

        val mathTotal = findMathTotal(allAmounts)
        if (mathTotal != null) return mathTotal

        return allAmounts.filter { it <= 20_000_000.0 }.maxOrNull()
    }

    private fun findMathTotal(amountsInOrder: List<Double>): Double? {
        val distinctAmounts = amountsInOrder.distinct()
        val sorted = distinctAmounts.sorted()

        if (sorted.size >= 3) {
            val possibleTotals = mutableListOf<Double>()
            for (i in 0 until sorted.size - 2) {
                for (j in i + 1 until sorted.size - 1) {
                    for (k in j + 1 until sorted.size) {
                        val a = sorted[i]
                        val b = sorted[j]
                        val c = sorted[k]
                        if (Math.abs((a + b) - c) < 1.0) {
                            val idxA = amountsInOrder.indexOf(a)
                            val idxB = amountsInOrder.indexOf(b)
                            val idxC = amountsInOrder.indexOf(c)

                            val minIdxPart = Math.min(idxA, idxB)
                            val maxIdxPart = Math.max(idxA, idxB)

                            if (idxC > maxIdxPart) {
                                possibleTotals.add(c)
                            } else if (idxC in minIdxPart..maxIdxPart) {
                                val total = if (idxA < idxB) a else b
                                possibleTotals.add(total)
                            } else {
                                possibleTotals.add(c)
                            }
                        }
                    }
                }
            }
            if (possibleTotals.isNotEmpty()) {
                return possibleTotals.maxOrNull()
            }
        }
        return null
    }

    private fun extractAmountsFromLine(line: String, isPriority: Boolean): List<Double> {
        val moneyRegex = if (isPriority) {
            Regex("""(?i)(?:rp\s*)?(?<!\d)[1-9]\d{0,2}(?:[.,]\d{3})+(?:[.,]\d{2})?(?!\d)|(?:rp\s*)?(?<!\d)[1-9]\d{2,7}(?!\d)""")
        } else {
            Regex("""(?i)(?:rp\s*)?(?<!\d)[1-9]\d{0,2}(?:[.,]\d{3})+(?:[.,]\d{2})?(?!\d)|(?:rp\s*)(?<!\d)[1-9]\d{2,7}(?!\d)""")
        }

        return moneyRegex.findAll(line)
            .mapNotNull { matchResult ->
                parseMoney(matchResult.value)
            }
            .filter { amount ->
                amount >= 100.0
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
            }
        }

        return null
    }

    private data class ParsedDate(
        val dateText: String = "",
        val dateMillis: Long? = null
    )

    private fun guessCategory(merchant: String): String? {
        if (merchant.isBlank()) return null

        val lowerMerchant = merchant.lowercase()
        val foodKeywords = listOf("resto", "cafe", "kopi", "warung", "makan", "ayam", "bakso", "kfc", "mcd", "starbucks", "mixue")
        val shoppingKeywords = listOf("indomaret", "alfamart", "supermarket", "mall", "mart", "toko", "grocery", "minimarket")

        if (foodKeywords.any { lowerMerchant.contains(it) }) {
            return "Makan"
        }

        if (shoppingKeywords.any { lowerMerchant.contains(it) }) {
            return "Belanja"
        }

        return null
    }
}
