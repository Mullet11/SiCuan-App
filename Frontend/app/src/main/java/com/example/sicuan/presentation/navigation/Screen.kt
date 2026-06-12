package com.example.sicuan.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Dashboard : Screen("dashboard")

    data object TransactionList : Screen("transaction_list")
    data object AddTransaction : Screen("add_transaction")
    data object ScanReceipt : Screen("scan_receipt")

    data object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        const val ARG_TRANSACTION_ID = "transactionId"

        fun createRoute(transactionId: Int): String {
            return "transaction_detail/$transactionId"
        }
    }

    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        const val ARG_TRANSACTION_ID = "transactionId"

        fun createRoute(transactionId: Int): String {
            return "edit_transaction/$transactionId"
        }
    }

    data object Budget : Screen("budget")
    data object Insight : Screen("insight")
    data object Profile : Screen("profile")
}