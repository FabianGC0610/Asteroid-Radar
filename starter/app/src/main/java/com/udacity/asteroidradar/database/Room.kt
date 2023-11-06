package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM asteroids_table WHERE close_approach_date = date('now','-1 day')")
    suspend fun deleteOldAsteroids()

    @Query("SELECT * FROM asteroids_table ORDER BY close_approach_date DESC")
    fun getSavedAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE close_approach_date <= date('now') AND close_approach_date >= date('now', '-7 days') ORDER BY close_approach_date DESC")
    fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE close_approach_date = date('now')")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids",
            ).build()
        }
    }
    return INSTANCE
}
