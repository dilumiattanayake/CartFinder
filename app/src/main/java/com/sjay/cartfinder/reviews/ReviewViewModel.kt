package com.sjay.cartfinder.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Review
import com.sjay.cartfinder.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReviewState {
    object Idle : ReviewState()
    object Loading : ReviewState()
    data class Success(val reviews: List<Review>) : ReviewState()
    data class Error(val message: String) : ReviewState()
}

sealed class SubmitReviewState {
    object Idle : SubmitReviewState()
    object Submitting : SubmitReviewState()
    object Success : SubmitReviewState()
    data class Error(val message: String) : SubmitReviewState()
}

class ReviewViewModel(
    private val repository: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _reviewsState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val reviewsState: StateFlow<ReviewState> = _reviewsState.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitReviewState>(SubmitReviewState.Idle)
    val submitState: StateFlow<SubmitReviewState> = _submitState.asStateFlow()

    fun getReviewsForStall(stallId: String) {
        _reviewsState.value = ReviewState.Loading
        viewModelScope.launch {
            val result = repository.getReviewsForStall(stallId)
            if (result.isSuccess) {
                val reviews = result.getOrNull() ?: emptyList()
                _reviewsState.value = ReviewState.Success(reviews)
            } else {
                _reviewsState.value = ReviewState.Error(result.exceptionOrNull()?.message ?: "Failed to fetch reviews")
            }
        }
    }

    fun submitReview(orderId: String, customerId: String, stallId: String, rating: Int, comment: String, imageUrls: List<String> = emptyList()) {
        if (rating < 1 || rating > 5) {
            _submitState.value = SubmitReviewState.Error("Rating must be between 1 and 5")
            return
        }
        if (comment.isBlank()) {
            _submitState.value = SubmitReviewState.Error("Comment cannot be empty")
            return
        }

        _submitState.value = SubmitReviewState.Submitting
        viewModelScope.launch {
            val review = Review(
                orderId = orderId,
                customerId = customerId,
                stallId = stallId,
                rating = rating,
                comment = comment,
                imageUrls = imageUrls
            )
            
            val result = repository.addReview(review)
            if (result.isSuccess) {
                _submitState.value = SubmitReviewState.Success
                // Optionally refresh reviews if we are on the stall page
                getReviewsForStall(stallId)
            } else {
                _submitState.value = SubmitReviewState.Error(result.exceptionOrNull()?.message ?: "Failed to submit review")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = SubmitReviewState.Idle
    }

    fun editReview(review: Review, newRating: Int, newComment: String, stallId: String) {
        if (newRating < 1 || newRating > 5) return
        if (newComment.isBlank()) return

        viewModelScope.launch {
            val updated = review.copy(rating = newRating, comment = newComment)
            repository.updateReview(updated)
            getReviewsForStall(stallId)
        }
    }

    fun reportReview(reviewId: String, stallId: String) {
        viewModelScope.launch {
            repository.reportReview(reviewId)
            getReviewsForStall(stallId)
        }
    }

    fun deleteReview(reviewId: String, stallId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            getReviewsForStall(stallId)
        }
    }

    fun replyToReview(reviewId: String, reply: String, stallId: String) {
        if (reply.isBlank()) return
        viewModelScope.launch {
            repository.replyToReview(reviewId, reply)
            getReviewsForStall(stallId)
        }
    }

    fun toggleLike(reviewId: String, userId: String, stallId: String) {
        viewModelScope.launch {
            repository.toggleLike(reviewId, userId)
            getReviewsForStall(stallId)
        }
    }

    fun toggleDislike(reviewId: String, userId: String, stallId: String) {
        viewModelScope.launch {
            repository.toggleDislike(reviewId, userId)
            getReviewsForStall(stallId)
        }
    }
}
