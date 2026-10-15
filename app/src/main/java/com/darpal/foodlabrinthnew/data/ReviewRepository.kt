package com.darpal.foodlabrinthnew.data

import com.darpal.foodlabrinthnew.data.model.Photo
import com.darpal.foodlabrinthnew.data.model.Review
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Reviews and photos for a single restaurant, stored in the same nodes the original app used. */
class ReviewRepository(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
) {
    fun reviews(businessId: String): Flow<List<Review>> =
        database.reference.child(REVIEWS).orderByChild("business_id").equalTo(businessId)
            .snapshots()
            .map { snapshot ->
                snapshot.childMaps()
                    .mapNotNull { (key, map) -> Review.fromMap(key, map) }
                    .sortedByDescending { it.date }
                    .toList()
            }

    fun photos(businessId: String): Flow<List<Photo>> =
        database.reference.child(PHOTOS).orderByChild("business_id").equalTo(businessId)
            .snapshots()
            .map { snapshot ->
                snapshot.childMaps().mapNotNull { (key, map) -> Photo.fromMap(key, map) }.toList().reversed()
            }

    suspend fun addReview(businessId: String, stars: Float, text: String, user: AuthUser) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val review = mapOf(
            "business_id" to businessId,
            "date" to date,
            "text" to text,
            "userid" to user.uid,
            "user_name" to user.label,
            // Stored as a string, like the original app, so older clients can still parse it.
            "stars" to stars.toString(),
        )
        database.reference.child(REVIEWS).push().setValue(review).await()
    }

    /** Uploads a JPEG to Cloud Storage and records its download URL under `photos`. */
    suspend fun uploadPhoto(businessId: String, jpeg: ByteArray, userId: String) {
        val ref = storage.reference.child("photos${System.currentTimeMillis()}.jpg")
        ref.putBytes(jpeg).await()
        val url = ref.downloadUrl.await().toString()
        val photo = mapOf("imageUrl" to url, "user_id" to userId, "business_id" to businessId)
        database.reference.child(PHOTOS).push().setValue(photo).await()
    }

    private companion object {
        const val REVIEWS = "reviews"
        const val PHOTOS = "photos"
    }
}
