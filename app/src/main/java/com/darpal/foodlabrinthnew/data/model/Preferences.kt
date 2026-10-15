package com.darpal.foodlabrinthnew.data.model

/** Answers for the "Help me choose" questionnaire. */
enum class Meal(val label: String, val keys: Set<String>) {
    BREAKFAST("Breakfast", setOf("breakfast", "brunch")),
    LUNCH("Lunch", setOf("lunch")),
    DINNER("Dinner", setOf("dinner")),
    CAFE("Cafe", setOf("dessert", "brunch")),
    LATE_NIGHT("Late night", setOf("latenight")),
}

/** Mapped onto Yelp `Ambience` keys; the mapping is a best-effort heuristic. */
enum class Ambience(val label: String, val keys: Set<String>) {
    LUXURY("Luxury", setOf("upscale", "classy")),
    ROMANTIC("Romantic", setOf("romantic", "intimate")),
    TRENDY("Trendy", setOf("trendy", "hipster")),
    CASUAL("Casual", setOf("casual")),
    HANGOUT("Hangout", setOf("casual", "divey")),
}

enum class Budget(val label: String, val description: String, val priceRanges: Set<Int>) {
    LOW("$", "Easy on the wallet", setOf(1)),
    MEDIUM("$$", "Mid-range", setOf(2)),
    HIGH("$$$", "Treat yourself", setOf(3, 4)),
}
