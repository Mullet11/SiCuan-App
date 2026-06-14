package com.example.sicuan.data.remote.firebase

import com.example.sicuan.domain.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreTransactionRemoteDataSource(
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

    suspend fun backupTransaction(transaction: Transaction) {
        val userId = getOrCreateUserId() ?: return

        val transactionData = hashMapOf(
            "id" to transaction.id,
            "title" to transaction.title,
            "amount" to transaction.amount,
            "type" to transaction.type,
            "category" to transaction.category,
            "date" to transaction.date,
            "note" to transaction.note,
            "merchant" to transaction.merchant,
            "createdAt" to transaction.createdAt,
            "updatedAt" to transaction.updatedAt
        )

        firestore
            .collection("users")
            .document(userId)
            .collection("transactions")
            .document(transaction.id.toString())
            .set(transactionData)
            .await()
    }

    suspend fun deleteTransaction(transactionId: Int) {
        val userId = getOrCreateUserId() ?: return

        firestore
            .collection("users")
            .document(userId)
            .collection("transactions")
            .document(transactionId.toString())
            .delete()
            .await()
    }
}