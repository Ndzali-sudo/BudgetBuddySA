package com.budgetbuddysa.app.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ReceiptStorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun uploadReceipt(
        imageUri: Uri,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onComplete(false, "Please sign in first.")
            return
        }

        val fileName = "receipt_${System.currentTimeMillis()}.jpg"

        val receiptReference = storage.reference
            .child("users/$userId/receipts/$fileName")

        receiptReference.putFile(imageUri)
            .addOnSuccessListener {
                receiptReference.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        onComplete(true, downloadUri.toString())
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message)
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}