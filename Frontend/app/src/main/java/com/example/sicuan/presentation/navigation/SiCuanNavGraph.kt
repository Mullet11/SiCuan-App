package com.example.sicuan.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navArgument
import com.example.sicuan.presentation.screen.plan.PlanScreen
import com.example.sicuan.presentation.screen.plan.AddPlanScreen
import com.example.sicuan.presentation.screen.plan.PlanDetailScreen
import com.example.sicuan.presentation.screen.dashboard.DashboardScreen
import com.example.sicuan.presentation.screen.education.EducationScreen
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
import com.example.sicuan.presentation.viewmodel.PlanViewModel
import com.example.sicuan.presentation.viewmodel.PlanViewModelFactory
import com.example.sicuan.presentation.viewmodel.PlanDetailViewModel
import com.example.sicuan.presentation.viewmodel.PlanDetailViewModelFactory
import com.example.sicuan.presentation.viewmodel.CurrencyViewModel
import com.example.sicuan.presentation.viewmodel.CurrencyViewModelFactory
import com.example.sicuan.presentation.viewmodel.FirebaseAuthViewModel
import com.example.sicuan.presentation.viewmodel.FirebaseAuthViewModelFactory
import com.example.sicuan.presentation.screen.ocr.ScanReceiptScreen
import com.example.sicuan.presentation.screen.ai.AiAssistantScreen
import com.example.sicuan.presentation.screen.auth.PinAuthScreen
import com.example.sicuan.presentation.component.SiCuanBottomNavBar
import com.example.sicuan.presentation.viewmodel.PinAuthViewModel
import com.example.sicuan.presentation.viewmodel.PinAuthViewModelFactory
import com.example.sicuan.presentation.viewmodel.ThemeViewModel

@Composable
fun SiCuanNavGraph(
    themeViewModel: ThemeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val firebaseAuthUseCases = remember { AppModule.provideFirebaseAuthUseCases() }
    val supabaseStorageRepository = remember { AppModule.provideSupabaseStorageRepository() }
    val sharedPreferences = remember { context.getSharedPreferences("sicuan_user_prefs", android.content.Context.MODE_PRIVATE) }
    
    val firebaseAuthViewModel: FirebaseAuthViewModel = viewModel(
        factory = FirebaseAuthViewModelFactory(firebaseAuthUseCases, supabaseStorageRepository, sharedPreferences)
    )

    val firebaseAuthUiState by firebaseAuthViewModel.uiState.collectAsStateWithLifecycle()

    val transactionUseCases = remember {
        AppModule.provideTransactionUseCases(context)
    }

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(transactionUseCases)
    )

    val transactionUiState by transactionViewModel.uiState.collectAsStateWithLifecycle()
    val selectedTransaction by transactionViewModel.selectedTransaction.collectAsStateWithLifecycle()

    val planUseCases = remember {
        AppModule.providePlanUseCases(context)
    }

    val planViewModel: PlanViewModel = viewModel(
        factory = PlanViewModelFactory(planUseCases)
    )

    val planUiState by planViewModel.uiState.collectAsStateWithLifecycle()

    val currencyUseCases = remember {
        AppModule.provideCurrencyUseCases()
    }

    val currencyViewModel: CurrencyViewModel = viewModel(
        factory = CurrencyViewModelFactory(currencyUseCases)
    )

    val currencyUiState by currencyViewModel.uiState.collectAsStateWithLifecycle()

    val pinRepository = remember {
        AppModule.providePinRepository(context)
    }
    
    val pinAuthViewModel: PinAuthViewModel = viewModel(
        factory = PinAuthViewModelFactory(pinRepository, sharedPreferences)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Insight.route,
        Screen.Plan.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                SiCuanBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.ScanReceipt.route) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = "Scan Struk")
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        if (firebaseAuthUiState.isSignedIn && !firebaseAuthUiState.isAnonymous) {
                            navController.navigate(Screen.PinAuth.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                com.example.sicuan.presentation.screen.onboarding.OnboardingScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Auth.route) {
                com.example.sicuan.presentation.screen.auth.AuthScreen(
                    viewModel = firebaseAuthViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.PinAuth.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.PinAuth.route) {
                PinAuthScreen(
                    viewModel = pinAuthViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.PinAuth.route) {
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
                    currencyRates = currencyUiState.rates,
                    plans = planUiState.plans,
                    userName = firebaseAuthUiState.username?.takeIf { it.isNotBlank() } ?: firebaseAuthUiState.displayName,
                    photoUrl = firebaseAuthUiState.photoUrl,
                    onNavigateToTransactions = {
                        navController.navigate(Screen.TransactionList.route)
                    },
                    onNavigateToTransactionDetail = { transactionId ->
                        navController.navigate(
                            Screen.TransactionDetail.createRoute(transactionId)
                        )
                    },
                    onNavigateToBudget = {
                        navController.navigate(Screen.Plan.route)
                    },
                    onNavigateToInsight = {
                        navController.navigate(Screen.Insight.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToAiAssistant = {
                        navController.navigate(Screen.AiAssistant.route)
                    },
                    onInputClick = { amount ->
                        if (amount != null) {
                            transactionViewModel.setPrefilledData(amount = amount)
                        }
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onNavigateToEdukasi = {
                        navController.navigate(Screen.Education.route)
                    }
                )
            }

            composable(Screen.Education.route) {
                EducationScreen(
                    onBack = { navController.popBackStack() }
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
                    prefilledAmount = transactionUiState.prefilledAmount,
                    prefilledTitle = transactionUiState.prefilledTitle,
                    prefilledNote = transactionUiState.prefilledNote,
                    prefilledMerchant = transactionUiState.prefilledMerchant,
                    prefilledCategory = transactionUiState.prefilledCategory,
                    prefilledDateMillis = transactionUiState.prefilledDateMillis,
                    onClearMessage = {
                        transactionViewModel.clearMessage()
                    },
                    onBack = {
                        transactionViewModel.clearMessage()
                        navController.popBackStack()
                    },
                    onSaveTransaction = { title, amount, type, category, note, merchant, dateMillis ->
                        transactionViewModel.addTransaction(
                            title = title,
                            amountText = amount,
                            type = type,
                            category = category,
                            note = note,
                            merchant = merchant,
                            dateMillis = dateMillis,
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }

            composable(Screen.ScanReceipt.route) {
                ScanReceiptScreen(
                    transactionErrorMessage = transactionUiState.errorMessage,
                    onClearMessage = {
                        transactionViewModel.clearMessage()
                    },
                    onNavigateToManualInput = {
                        navController.navigate(Screen.AddTransaction.route) {
                            popUpTo(Screen.ScanReceipt.route) { inclusive = true }
                        }
                    },
                    onNavigateToAddTransactionWithData = { amount, title, note, merchant, date, category ->
                        transactionViewModel.setPrefilledData(
                            amount = amount,
                            title = title,
                            note = note,
                            merchant = merchant,
                            category = category,
                            dateMillis = date
                        )
                        navController.navigate(Screen.AddTransaction.route) {
                            popUpTo(Screen.ScanReceipt.route) { inclusive = true }
                        }
                    },
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

            composable(Screen.Plan.route) {
                PlanScreen(
                    plans = planUiState.plans,
                    userName = firebaseAuthUiState.username?.takeIf { it.isNotBlank() } ?: firebaseAuthUiState.displayName,
                    photoUrl = firebaseAuthUiState.photoUrl,
                    onNavigateToAddPlan = {
                        navController.navigate(Screen.AddPlan.route)
                    },
                    onNavigateToPlanDetail = { planId ->
                        navController.navigate(Screen.PlanDetail.createRoute(planId))
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AddPlan.route) {
                AddPlanScreen(
                    onBack = {
                        planViewModel.clearMessage()
                        navController.popBackStack()
                    },
                    onSave = { title, amount, deadline ->
                        planViewModel.addPlan(
                            title = title,
                            targetAmountText = amount,
                            deadlineDateMillis = deadline,
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    },
                    errorMessage = planUiState.errorMessage,
                    onClearMessage = {
                        planViewModel.clearMessage()
                    }
                )
            }

            composable(Screen.Insight.route) {
                InsightScreen(
                    userName = firebaseAuthUiState.username?.takeIf { it.isNotBlank() } ?: firebaseAuthUiState.displayName,
                    photoUrl = firebaseAuthUiState.photoUrl,
                    transactions = transactionUiState.transactions,
                    currencyRates = currencyUiState.rates,
                    exportedReports = transactionUiState.exportedReports,
                    isLoading = currencyUiState.isLoading,
                    errorMessage = currencyUiState.errorMessage,
                    onRefresh = {
                        currencyViewModel.fetchLatestRates()
                    },
                    onNavigateToEducation = {
                        navController.navigate(Screen.Education.route)
                    },
                    onDeleteReport = { reportId ->
                        transactionViewModel.deleteReport(reportId)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    firebaseAuthUiState = firebaseAuthUiState,
                    themeViewModel = themeViewModel,
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile.route)
                    },
                    onNavigateToChangePassword = {
                        navController.navigate(Screen.ChangePassword.route)
                    },
                    onExportReport = {
                        navController.navigate(Screen.ReportSummary.route)
                    },
                    onSignOut = {
                        firebaseAuthViewModel.signOut()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0)
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            composable(Screen.EditProfile.route) {
                com.example.sicuan.presentation.screen.profile.EditProfileScreen(
                    currentName = firebaseAuthUiState.displayName ?: "",
                    currentUsername = firebaseAuthUiState.username ?: "",
                    currentEmail = firebaseAuthUiState.email ?: "",
                    currentPhone = firebaseAuthUiState.phone ?: "",
                    currentDob = firebaseAuthUiState.dob ?: "",
                    currentBio = firebaseAuthUiState.bio ?: "",
                    photoUrl = firebaseAuthUiState.photoUrl,
                    onSave = { name, username, email, phone, dob, bio, newPhotoUrl ->
                        firebaseAuthViewModel.updateFullProfile(name, username, email, phone, dob, bio, newPhotoUrl)
                        navController.popBackStack()
                    },
                    onPhotoSelected = { uri ->
                        // Optional: if you want to handle photo saving globally
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ChangePassword.route) {
                com.example.sicuan.presentation.screen.profile.ChangePasswordScreen(
                    onSave = { oldPass, newPass ->
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ReportSummary.route) {
                val expenseTransactions = transactionUiState.transactions.filter { it.type == com.example.sicuan.domain.model.TransactionType.EXPENSE }
                val totalExp = transactionUiState.totalExpense.coerceAtLeast(1.0)
                val categoryMap = expenseTransactions.groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }
                    .entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .map { (cat, amt) ->
                        com.example.sicuan.presentation.viewmodel.CategorySummary(
                            name = cat,
                            amount = amt,
                            percentage = ((amt / totalExp) * 100).toFloat()
                        )
                    }

                com.example.sicuan.presentation.screen.profile.ReportSummaryScreen(
                    totalIncome = transactionUiState.totalIncome,
                    totalExpense = transactionUiState.totalExpense,
                    balance = transactionUiState.balance,
                    transactionCount = transactionUiState.transactions.size,
                    topCategories = categoryMap,
                    onAddToReport = {
                        transactionViewModel.exportReport()
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(
                    transactions = transactionUiState.transactions,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.PlanDetail.route,
                arguments = listOf(
                    navArgument("planId") { type = androidx.navigation.NavType.IntType }
                )
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: return@composable

                val planDetailViewModel: PlanDetailViewModel = viewModel(
                    factory = PlanDetailViewModelFactory(planId, planUseCases)
                )
                val planDetailUiState by planDetailViewModel.uiState.collectAsStateWithLifecycle()

                PlanDetailScreen(
                    plan = planDetailUiState.plan,
                    histories = planDetailUiState.histories,
                    errorMessage = planDetailUiState.errorMessage,
                    successMessage = planDetailUiState.successMessage,
                    onBack = {
                        planDetailViewModel.clearMessage()
                        navController.popBackStack()
                    },
                    onTopUp = { amount ->
                        planDetailViewModel.topUpPlan(amount)
                    },
                    onWithdraw = { amount ->
                        planDetailViewModel.withdrawPlan(amount)
                    },
                    onDelete = {
                        planDetailViewModel.deletePlan()
                        navController.popBackStack()
                    },
                    onClearMessage = {
                        planDetailViewModel.clearMessage()
                    }
                )
            }
        }
    }
}