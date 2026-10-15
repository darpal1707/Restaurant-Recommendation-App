package com.darpal.foodlabrinthnew.data.model

/**
 * One rule of an OSM `opening_hours` value, prepared for display.
 *
 * Only the common subset of the specification (day ranges + time ranges) is understood; anything
 * else is shown verbatim rather than guessed at.
 */
data class OpeningHours(val day: String, val time: String) {
    companion object {
        private val DAY_NAMES = mapOf(
            "Mo" to "Mon", "Tu" to "Tue", "We" to "Wed", "Th" to "Thu",
            "Fr" to "Fri", "Sa" to "Sat", "Su" to "Sun", "PH" to "Holidays", "SH" to "School holidays",
        )
        private val DAY_TOKEN = "(?:Mo|Tu|We|Th|Fr|Sa|Su|PH|SH)"
        private val RULE = Regex("""^($DAY_TOKEN(?:\s*-\s*$DAY_TOKEN)?(?:\s*,\s*$DAY_TOKEN(?:\s*-\s*$DAY_TOKEN)?)*)\s+(.+)$""")
        private val TIME_RANGE = Regex("""(\d{1,2}):(\d{2})\s*-\s*(\d{1,2}):(\d{2})""")

        fun parse(raw: String?): List<OpeningHours> {
            if (raw.isNullOrBlank()) return emptyList()
            return raw.split(';').map { it.trim() }.filter { it.isNotEmpty() }.map { rule ->
                if (rule == "24/7") return@map OpeningHours("Every day", "Open 24 hours")
                val match = RULE.find(rule) ?: return@map OpeningHours("", rule)
                val days = match.groupValues[1]
                    .replace(Regex("\\s*-\\s*"), "–")
                    .replace(Regex("\\s*,\\s*"), ", ")
                    .let { d -> Regex(DAY_TOKEN).replace(d) { DAY_NAMES.getValue(it.value) } }
                val time = match.groupValues[2].let { t ->
                    when (t.lowercase()) {
                        "off", "closed" -> "Closed"
                        else -> t.replace(Regex("\\s*-\\s*"), " – ").replace(Regex("\\s*,\\s*"), ", ")
                    }
                }
                OpeningHours(days, time)
            }
        }

        /**
         * Whether the place is open at [minuteOfDay] (0..1439) on at least one day of the week.
         * Days are ignored on purpose: the questionnaire asks "a lunch place", not "open this Tuesday".
         * Returns null when the value can't be interpreted, so callers can treat it as unknown.
         */
        fun isOpenAt(raw: String?, minuteOfDay: Int): Boolean? {
            if (raw.isNullOrBlank()) return null
            if (raw.trim() == "24/7") return true
            val ranges = raw.split(';')
                .map { it.trim() }
                .filterNot { it.endsWith(" off", ignoreCase = true) || it.endsWith(" closed", ignoreCase = true) }
                .flatMap { rule -> TIME_RANGE.findAll(rule).toList() }
                .map { m ->
                    val (h1, m1, h2, m2) = m.destructured
                    (h1.toInt() * 60 + m1.toInt()) to (h2.toInt() * 60 + m2.toInt())
                }
            if (ranges.isEmpty()) return null
            return ranges.any { (start, end) ->
                if (start < end) minuteOfDay in start until end
                else minuteOfDay >= start || minuteOfDay < end // crosses midnight, e.g. 18:00-02:00
            }
        }
    }
}
