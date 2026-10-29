package com.example.core.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.model.NewsArticle
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_news_articles")
data class CachedNewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val imageUrl: String,
    val categoryId: String,
    val author: String,
    val sourceUrl: String,
    val readingTimeMinutes: Int,
    val tagsCsv: String,
    val cachedAtMillis: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): NewsArticle = NewsArticle(
        id = id,
        title = title,
        summary = summary,
        content = content,
        imageUrl = imageUrl,
        categoryId = categoryId,
        author = author,
        status = "published",
        sourceUrl = sourceUrl,
        readingTimeMinutes = readingTimeMinutes,
        tags = if (tagsCsv.isBlank()) emptyList() else tagsCsv.split(",").map { it.trim() }
    )
}

fun NewsArticle.toCachedEntity(): CachedNewsEntity = CachedNewsEntity(
    id = id,
    title = title,
    summary = summary,
    content = content,
    imageUrl = imageUrl,
    categoryId = categoryId,
    author = author,
    sourceUrl = sourceUrl,
    readingTimeMinutes = readingTimeMinutes,
    tagsCsv = tags.joinToString(",")
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Dao
interface PenolesLocalDao {
    @Query("SELECT * FROM cached_news_articles ORDER BY cachedAtMillis DESC")
    fun observeCachedNews(): Flow<List<CachedNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedNews(articles: List<CachedNewsEntity>)

    @Query("SELECT * FROM search_history ORDER BY timestampMillis DESC LIMIT 10")
    fun observeRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}

@Database(
    entities = [CachedNewsEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PenolesLocalDatabase : RoomDatabase() {
    abstract fun penolesLocalDao(): PenolesLocalDao

    companion object {
        @Volatile
        private var INSTANCE: PenolesLocalDatabase? = null

        fun getInstance(context: Context): PenolesLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PenolesLocalDatabase::class.java,
                    "penoles_conecta_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
