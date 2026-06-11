package com.example.sicuan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sicuan.presentation.screen.budget.BudgetScreen
import com.example.sicuan.presentation.screen.dashboard.DashboardScreen
import com.example.sicuan.presentation.screen.insight.InsightScreen
import com.example.sicuan.presentation.screen.profile.ProfileScreen
import com.example.sicuan.presentation.screen.splash.SplashScreen
import com.example.sicuan.presentation.screen.transaction.AddTransactionScreen
import com.example.sicuan.presentation.screen.transaction.EditTransactionScreen
import com.example.sicuan.presentation.screen.transaction.TransactionDetailScreen
import com.example.sicuan.presentation.screen.transaction.TransactionListScreen

@Composable
fun SiCuanNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = {
                    navController.navigate(Screen.TransactionList.route)
                },
                onNavigateToBudget = {
                    navController.navigate(Screen.Budget.route)
                },
                onNavigateToInsight = {
                    navController.navigate(Screen.Insight.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToDetailTransaction = { transactionId ->
                    navController.navigate(
                        Screen.TransactionDetail.createRoute(transactionId)
                    )
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument(Screen.TransactionDetail.ARG_TRANSACTION_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getInt(Screen.TransactionDetail.ARG_TRANSACTION_ID)
                ?: 0

            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditTransaction.createRoute(id))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument(Screen.EditTransaction.ARG_TRANSACTION_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getInt(Screen.EditTransaction.ARG_TRANSACTION_ID)
                ?: 0

            EditTransactionScreen(
                transactionId = transactionId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Budget.route) {
            BudgetScreen()
        }

        composable(Screen.Insight.route) {
            InsightScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}