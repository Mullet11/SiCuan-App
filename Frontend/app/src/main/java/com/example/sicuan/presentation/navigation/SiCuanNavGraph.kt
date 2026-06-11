package com.example.sicuan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.sicuan.di.AppModule
import com.example.sicuan.presentation.viewmodel.TransactionViewModel
import com.example.sicuan.presentation.viewmodel.TransactionViewModelFactory
import com.example.sicuan.presentation.viewmodel.BudgetViewModel
import com.example.sicuan.presentation.viewmodel.BudgetViewModelFactory
import com.example.sicuan.presentation.viewmodel.CurrencyViewModel
import com.example.sicuan.presentation.viewmodel.CurrencyViewModelFactory

@Composable
fun SiCuanNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val transactionUseCases = remember {
        AppModule.provideTransactionUseCases(context)
    }

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(transactionUseCases)
    )

    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val selectedTransaction by transactionViewModel.selectedTransaction.collectAsState()

    val budgetUseCases = remember {
        AppModule.provideBudgetUseCases(context)
    }

    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(budgetUseCases)
    )

    val budgetUiState by budgetViewModel.uiState.collectAsState()

    val currencyUseCases = remember {
        AppModule.provideCurrencyUseCases()
    }

    val currencyViewModel: CurrencyViewModel = viewModel(
        factory = CurrencyViewModelFactory(currencyUseCases)
    )

    val currencyUiState by currencyViewModel.uiState.collectAsState()

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
                balance = transactionUiState.balance,
                totalIncome = transactionUiState.totalIncome,
                totalExpense = transactionUiState.totalExpense,
                allTransactions = transactionUiState.transactions,
                recentTransactions = transactionUiState.transactions.take(3),
                onNavigateToTransactions = {
                    navController.navigate(Screen.TransactionList.route)
                },
                onNavigateToTransactionDetail = { transactionId ->
                    navController.navigate(
                        Screen.TransactionDetail.createRoute(transactionId)
                    )
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
                transactions = transactionUiState.transactions,
                isLoading = transactionUiState.isLoading,
                errorMessage = transactionUiState.errorMessage,
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
                errorMessage = transactionUiState.errorMessage,
                onClearMessage = {
                    transactionViewModel.clearMessage()
                },
                onBack = {
                    transactionViewModel.clearMessage()
                    navController.popBackStack()
                },
                onSaveTransaction = { title, amount, type, category, note, merchant ->
                    transactionViewModel.addTransaction(
                        title = title,
                        amountText = amount,
                        type = type,
                        category = category,
                        note = note,
                        merchant = merchant,
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
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

            LaunchedEffect(transactionId) {
                transactionViewModel.observeTransactionById(transactionId)
            }

            TransactionDetailScreen(
                transactionId = transactionId,
                transaction = selectedTransaction,
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditTransaction.createRoute(id))
                },
                onDelete = {
                    transactionViewModel.deleteTransaction(
                        transactionId = transactionId,
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
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

            LaunchedEffect(transactionId) {
                transactionViewModel.observeTransactionById(transactionId)
            }

            EditTransactionScreen(
                transactionId = transactionId,
                transaction = selectedTransaction,
                errorMessage = transactionUiState.errorMessage,
                onClearMessage = {
                    transactionViewModel.clearMessage()
                },
                onUpdateTransaction = { title, amount, type, category, note, merchant ->
                    transactionViewModel.updateSelectedTransaction(
                        title = title,
                        amountText = amount,
                        type = type,
                        category = category,
                        note = note,
                        merchant = merchant,
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                },
                onBack = {
                    transactionViewModel.clearMessage()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Budget.route) {
            BudgetScreen(
                budgets = budgetUiState.budgets,
                transactions = transactionUiState.transactions,
                isLoading = budgetUiState.isLoading,
                errorMessage = budgetUiState.errorMessage,
                successMessage = budgetUiState.successMessage,
                onClearMessage = {
                    budgetViewModel.clearMessage()
                },
                onAddBudget = { category, limitAmount ->
                    budgetViewModel.addBudget(
                        category = category,
                        limitAmountText = limitAmount
                    )
                },
                onUpdateBudget = { budgetId, category, limitAmount ->
                    budgetViewModel.updateBudget(
                        budgetId = budgetId,
                        category = category,
                        limitAmountText = limitAmount
                    )
                },
                onDeleteBudget = { budgetId ->
                    budgetViewModel.deleteBudget(budgetId)
                },
                onBack = {
                    budgetViewModel.clearMessage()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Insight.route) {
            InsightScreen(
                currencyRates = currencyUiState.rates,
                isLoading = currencyUiState.isLoading,
                errorMessage = currencyUiState.errorMessage,
                onRefresh = {
                    currencyViewModel.fetchLatestRates()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}