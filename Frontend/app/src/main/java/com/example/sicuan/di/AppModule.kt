package com.example.sicuan.di

import android.content.Context
import com.example.sicuan.data.local.database.SiCuanDatabase
import com.example.sicuan.data.repository.TransactionRepositoryImpl
import com.example.sicuan.domain.repository.TransactionRepository
import com.example.sicuan.domain.usecase.transaction.AddTransactionUseCase
import com.example.sicuan.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.sicuan.domain.usecase.transaction.GetAllTransactionsUseCase
import com.example.sicuan.domain.usecase.transaction.GetRecentTransactionsUseCase
import com.example.sicuan.domain.usecase.transaction.GetTotalAmountByTypeUseCase
import com.example.sicuan.domain.usecase.transaction.GetTransactionByIdUseCase
import com.example.sicuan.domain.usecase.transaction.TransactionUseCases
import com.example.sicuan.domain.usecase.transaction.UpdateTransactionUseCase
import com.example.sicuan.data.repository.PlanRepositoryImpl
import com.example.sicuan.domain.repository.PlanRepository
import com.example.sicuan.domain.usecase.plan.AddPlan
import com.example.sicuan.domain.usecase.plan.DeletePlan
import com.example.sicuan.domain.usecase.plan.GetPlans
import com.example.sicuan.domain.usecase.plan.PlanUseCases
import com.example.sicuan.domain.usecase.plan.TopUpPlan
import com.example.sicuan.domain.usecase.plan.UpdatePlan
import com.example.sicuan.domain.usecase.plan.WithdrawPlan
import com.example.sicuan.domain.usecase.plan.GetPlanHistory
import com.example.sicuan.data.remote.api.CurrencyApiService
import com.example.sicuan.data.remote.api.RetrofitClient
import com.example.sicuan.data.repository.CurrencyRepositoryImpl
import com.example.sicuan.domain.repository.CurrencyRepository
import com.example.sicuan.domain.usecase.currency.CurrencyUseCases
import com.example.sicuan.domain.usecase.currency.GetLatestCurrencyRatesUseCase
import com.example.sicuan.data.repository.FirebaseAuthRepositoryImpl
import com.example.sicuan.domain.repository.FirebaseAuthRepository
import com.example.sicuan.domain.usecase.auth.FirebaseAuthUseCases
import com.example.sicuan.domain.usecase.auth.GetCurrentFirebaseUserUseCase
import com.example.sicuan.domain.usecase.auth.SignInWithEmailAndPasswordUseCase
import com.example.sicuan.domain.usecase.auth.SignUpWithEmailAndPasswordUseCase
import com.example.sicuan.domain.usecase.auth.SignOutUseCase
import com.google.firebase.auth.FirebaseAuth
import com.example.sicuan.data.remote.firebase.FirestoreTransactionRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sicuan.domain.usecase.auth.UpdateProfileUseCase
import com.example.sicuan.data.repository.SupabaseStorageRepositoryImpl
import com.example.sicuan.domain.repository.SupabaseStorageRepository
import okhttp3.OkHttpClient

object AppModule {

    fun provideDatabase(context: Context): SiCuanDatabase {
        return SiCuanDatabase.getDatabase(context)
    }

    fun provideTransactionRepository(context: Context): TransactionRepository {
        val database = provideDatabase(context)

        return TransactionRepositoryImpl(
            transactionDao = database.transactionDao(),
            firestoreTransactionRemoteDataSource = provideFirestoreTransactionRemoteDataSource()
        )
    }

    fun provideTransactionUseCases(context: Context): TransactionUseCases {
        val repository = provideTransactionRepository(context)

        return TransactionUseCases(
            getAllTransactions = GetAllTransactionsUseCase(repository),
            getTransactionById = GetTransactionByIdUseCase(repository),
            addTransaction = AddTransactionUseCase(repository),
            updateTransaction = UpdateTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository),
            getTotalAmountByType = GetTotalAmountByTypeUseCase(repository),
            getRecentTransactions = GetRecentTransactionsUseCase(repository)
        )
    }

    fun providePlanRepository(context: Context): PlanRepository {
        val database = provideDatabase(context)

        return PlanRepositoryImpl(
            dao = database.planDao(),
            historyDao = database.planHistoryDao()
        )
    }

    fun providePlanUseCases(context: Context): PlanUseCases {
        val repository = providePlanRepository(context)

        return PlanUseCases(
            getPlans = GetPlans(repository),
            addPlan = AddPlan(repository),
            updatePlan = UpdatePlan(repository),
            deletePlan = DeletePlan(repository),
            topUpPlan = TopUpPlan(repository),
            withdrawPlan = WithdrawPlan(repository),
            getPlanHistory = GetPlanHistory(repository)
        )
    }

    fun provideCurrencyApiService(): CurrencyApiService {
        return RetrofitClient.currencyApiService
    }

    fun provideCurrencyRepository(): CurrencyRepository {
        return CurrencyRepositoryImpl(
            currencyApiService = provideCurrencyApiService()
        )
    }

    fun provideCurrencyUseCases(): CurrencyUseCases {
        val repository = provideCurrencyRepository()

        return CurrencyUseCases(
            getLatestCurrencyRates = GetLatestCurrencyRatesUseCase(repository)
        )
    }

    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun provideFirebaseAuthRepository(): FirebaseAuthRepository {
        return FirebaseAuthRepositoryImpl(
            firebaseAuth = provideFirebaseAuth()
        )
    }

    fun provideFirebaseAuthUseCases(): FirebaseAuthUseCases {
        val repository = provideFirebaseAuthRepository()

        return FirebaseAuthUseCases(
            signInWithEmailAndPassword = SignInWithEmailAndPasswordUseCase(repository),
            signUpWithEmailAndPassword = SignUpWithEmailAndPasswordUseCase(repository),
            signOut = SignOutUseCase(repository),
            getCurrentFirebaseUser = GetCurrentFirebaseUserUseCase(repository),
            updateProfile = UpdateProfileUseCase(repository)
        )
    }

    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    fun provideSupabaseStorageRepository(): SupabaseStorageRepository {
        return SupabaseStorageRepositoryImpl(provideOkHttpClient())
    }

    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun provideFirestoreTransactionRemoteDataSource(): FirestoreTransactionRemoteDataSource {
        return FirestoreTransactionRemoteDataSource(
            firestore = provideFirebaseFirestore(),
            firebaseAuth = provideFirebaseAuth()
        )
    }

    fun providePinRepository(context: Context): com.example.sicuan.domain.repository.PinRepository {
        return com.example.sicuan.data.local.PinRepositoryImpl(context)
    }
}