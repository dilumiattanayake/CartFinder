package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sjay.cartfinder.data.model.Review
import com.sjay.cartfinder.data.model.Notification
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewsCollection = firestore.collection("reviews")

    suspend fun addReview(review: Review): Result<String> {
        return try {
            val docRef = if (review.id.isEmpty()) {
                reviewsCollection.document("${review.stallId}_${review.customerId}")
            } else {
                reviewsCollection.document(review.id)
            }
            
            val reviewWithId = review.copy(id = docRef.id)
            docRef.set(reviewWithId).await()
            updateStallRating(review.stallId)
            
            // Notify Vendor
            try {
                val stallDoc = firestore.collection("stalls").document(review.stallId).get().await()
                val vendorId = stallDoc.getString("vendorId")
                if (vendorId != null) {
                    NotificationRepository().sendNotification(
                        Notification(
                            userId = vendorId,
                            title = "New Review",
                            message = "A customer left a ${review.rating}-star review for your stall.",
                            type = "NEW_REVIEW",
                            referenceId = docRef.id
                        )
                    )
                }
            } catch (e: Exception) {
                // Ignore notification failure
            }
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsForStall(stallId: String): Result<List<Review>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("stallId", stallId)
                .get()
                .await()
            
            val reviews = snapshot.toObjects(Review::class.java)
                .filter { it.status == "ACTIVE" }
                .sortedByDescending { it.createdAt }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(review: Review): Result<Unit> {
        return try {
            reviewsCollection.document(review.id)
                .set(review.copy(updatedAt = System.currentTimeMillis()))
                .await()
            updateStallRating(review.stallId)

            // Notify Customer if Vendor replied
            if (!review.vendorReply.isNullOrEmpty()) {
                try {
                    val stallDoc = firestore.collection("stalls").document(review.stallId).get().await()
                    val stallName = stallDoc.getString("name") ?: "Vendor"
                    NotificationRepository().sendNotification(
                        Notification(
                            userId = review.customerId,
                            title = "Vendor Replied",
                            message = "$stallName replied to your review.",
                            type = "REVIEW_REPLY",
                            referenceId = review.id
                        )
                    )
                } catch (e: Exception) {}
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewForOrder(orderId: String): Result<Review?> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("orderId", orderId)
                .limit(1)
                .get()
                .await()
            
            val review = snapshot.documents.firstOrNull()?.toObject(Review::class.java)
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId)
                .update(
                    mapOf(
                        "status" to "REPORTED",
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId)
                .update(
                    mapOf(
                        "status" to "ARCHIVED",
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            // We need to fetch the review to get the stallId to update the stall rating
            val reviewSnapshot = reviewsCollection.document(reviewId).get().await()
            reviewSnapshot.getString("stallId")?.let { stallId ->
                updateStallRating(stallId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun replyToReview(reviewId: String, reply: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId)
                .update(
                    mapOf(
                        "vendorReply" to reply,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(reviewId: String, userId: String): Result<Unit> {
        return try {
            val docRef = reviewsCollection.document(reviewId)
            val doc = docRef.get().await()
            val review = doc.toObject(Review::class.java)
            if (review != null) {
                val likedBy = review.likedBy.toMutableList()
                val dislikedBy = review.dislikedBy.toMutableList()

                if (likedBy.contains(userId)) {
                    likedBy.remove(userId)
                } else {
                    likedBy.add(userId)
                    dislikedBy.remove(userId) // Remove from dislike if liking
                }

                docRef.update(
                    mapOf(
                        "likedBy" to likedBy,
                        "dislikedBy" to dislikedBy,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleDislike(reviewId: String, userId: String): Result<Unit> {
        return try {
            val docRef = reviewsCollection.document(reviewId)
            val doc = docRef.get().await()
            val review = doc.toObject(Review::class.java)
            if (review != null) {
                val likedBy = review.likedBy.toMutableList()
                val dislikedBy = review.dislikedBy.toMutableList()

                if (dislikedBy.contains(userId)) {
                    dislikedBy.remove(userId)
                } else {
                    dislikedBy.add(userId)
                    likedBy.remove(userId) // Remove from like if disliking
                }

                docRef.update(
                    mapOf(
                        "likedBy" to likedBy,
                        "dislikedBy" to dislikedBy,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateStallRating(stallId: String) {
        try {
            val snapshot = reviewsCollection
                .whereEqualTo("stallId", stallId)
                .whereEqualTo("status", "ACTIVE")
                .get()
                .await()
            
            val reviews = snapshot.toObjects(Review::class.java)
            val count = reviews.size
            val average = if (count > 0) reviews.map { it.rating }.average() else 0.0
            
            firestore.collection("stalls").document(stallId)
                .update(
                    mapOf(
                        "ratingCount" to count,
                        "ratingAverage" to average
                    )
                ).await()
        } catch (e: Exception) {
            // Ignore error
        }
    }
}
