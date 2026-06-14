
import java.util.regex.Pattern

fun main() {
    val textAlfamart = """
        ALFAMART SULTAN ADAM 2
        NPWP : 01.336.238.9-054.000
        JL.SULTAN ADAM NO 18
        Bon 1G31-778-1006X3H9
        CHITATO SWD 65G 1 14,800 14,800
        MSCF CPCN 220ML 1 8,100 8,100
        Total Item 2 22,900
        DONASI-KU 100
        Total Belanja 23,000
        Tunai 25,000
        Kembalian 2,000
        PPN DPP: 20,630 PPN: 2,269
        KRITIK&SARAN:1500959
        SMS/WA: 081110640888
    """.trimIndent()
    
    val textBtn = """
        btn
        Transaksi Anda Berhasil Diproses.
        10/06/2026 - 13:01 WITA
        ID Transaksi TRX000559530366
        No. Ref ESB796104136
        Rekening Sumber Dana MUHAMMAD RAKHA 4610 1*** **29 76
        Jenis Transaksi QRIS
        Pembayaran ke QR OSHE COFFEE MBL - BANJARMASIN
        ID Terminal A01
        Nominal Rp 43.000,00
        Tips Rp 0,00
        Total Rp 43.000,00
    """.trimIndent()
    
    val amountRegex = Pattern.compile("(?:Rp\\s*)?\\b\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{1,2})?\\b", Pattern.CASE_INSENSITIVE)
    
    fun extract(text: String) {
        println("---")
        for (line in text.lines()) {
            val m = amountRegex.matcher(line)
            while (m.find()) {
                val str = m.group().replace("Rp", "").replace("rp", "").trim()
                val clean = str.replace(".", "").replace(",", "")
                println("Matched: ${m.group()} -> $clean")
            }
        }
    }
    
    println("ALFAMART")
    extract(textAlfamart)
    println("BTN")
    extract(textBtn)
}
