package com.example.sicuan.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Auth : Screen("auth")
    data object PinAuth : Screen("pin_auth")
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

    data object Plan : Screen("plan")
    data object AddPlan : Screen("add_plan")
    data object PlanDetail : Screen("plan_detail/{planId}") {
        fun createRoute(planId: Int) = "plan_detail/$planId"
    }
    data object Insight : Screen("insight")
    data object Profile : Screen("profile")
    data object AiAssistant : Screen("ai_assistant")
    data object Education : Screen("education")
    data object Quiz : Screen("quiz")
    data object EditProfile : Screen("edit_profile")
    data object ChangePassword : Screen("change_password")
    data object ReportSummary : Screen("report_summary")
}
