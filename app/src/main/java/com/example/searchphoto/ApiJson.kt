package com.example.searchphoto

data class TweetData(
    val `data`: List<Data?>,
    val includes: Includes?
)
data class Data(
    val attachments: Attachments?,
    val author_id: String?,
    val created_at: String?,
    val id: String?,
    val public_metrics: PublicMetrics?,
    val source: String?,
    val text: String?
)
data class Attachments(
    val media_keys: List<String?>?
)
data class Includes(
    val media: List<Media?>?
)
data class Media(
    val duration_ms: Int?,
    val media_key: String?,
    val public_metrics: PublicMetricsX?,
    val type: String?,
    val url: String?
)
data class PublicMetricsX(
    val view_count: Int?
)
data class PublicMetrics(
    val like_count: Int?,
    val quote_count: Int?,
    val reply_count: Int?,
    val retweet_count: Int?
)