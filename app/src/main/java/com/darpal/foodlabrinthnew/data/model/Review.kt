package com.darpal.foodlabrinthnew.data.model

/** A user review from the `reviews` node. Field names match what the original app wrote. */
data class Review(
    val id: String,
    val businessId: String,
    val text: String,
    val stars: Float,
    val date: String,
    val userId: String,
    val userName: String?,
) {
    companion object {
        fun fromMap(key: String?, map: Map<*, *>): Review? {
            val businessId = map["business_id"]?.toString() ?: return null
            val text = map["text"]?.toString()?.takeIf { it != "null" }.orEmpty()
            return Review(
                id = key ?: map["review_id"]?.toString().orEmpty(),
                businessId = businessId,
                text = text,
                stars = map["stars"]?.toString()?.toFloatOrNull()?.coerceIn(0f, 5f) ?: 0f,
                date = map["date"]?.toString()?.takeIf { it != "null" }.orEmpty(),
                userId = map["userid"]?.toString().orEmpty(),
                userName = map["user_name"]?.toString()?.takeIf { it.isNotBlank() && it != "null" },
            )
        }
    }
}

/** A user photo from the `photos` node. */
data class Photo(val id: String, val imageUrl: String, val businessId: String, val userId: String) {
    companion object {
        fun fromMap(key: String?, map: Map<*, *>): Photo? {
            val url = map["imageUrl"]?.toString()?.takeIf { it.startsWith("http") } ?: return null
            return Photo(
                id = key.orEmpty(),
                imageUrl = url,
                businessId = map["business_id"]?.toString().orEmpty(),
                userId = map["user_id"]?.toString().orEmpty(),
            )
        }
    }
}
