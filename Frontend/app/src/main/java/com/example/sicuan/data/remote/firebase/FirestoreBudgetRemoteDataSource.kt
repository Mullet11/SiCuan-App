package com.example.sicuan.data.remote.firebase

import com.example.sicuan.domain.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreBudgetRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private suspend fun getOrCreateUserId(): String? {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            return currentUser.uid
        }

        val result = firebaseAuth.signInAnonymously().await()
        return result.user?.uid
    }

    suspend fun backupBudget(budget: Budget) {
        val userId = getOrCreateUserId() ?: return

        val budgetData = hashMapOf(
            "id" to budget.id,
            "category" to budget.category,
            "limitAmount" to budget.limitAmount,
            "createdAt" to budget.createdAt,
            "updatedAt" to budget.updatedAt
        )

        firestore
            .collection("users")
            .document(userId)
            .collection("budgets")
            .document(budget.id.toString())
            .set(budgetData)
            .await()
    }

    suspend fun deleteBudget(budgetId: Int) {
        val userId = getOrCreateUserId() ?: return

        firestore
            .collection("users")
            .document(userId)
            .collection("budgets")
            .document(budgetId.toString())
            .delete()
            .await()
    }
}