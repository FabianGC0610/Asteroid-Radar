package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.AsteroidFilter
import com.udacity.asteroidradar.main.AsteroidsApiStatus
import com.udacity.asteroidradar.network.AsteroidApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val savedAsteroids: LiveData<List<Asteroid>> = database.asteroidDao.getSavedAsteroids().map {
        it.asDomainModel()
    }

    val weekAsteroids: LiveData<List<Asteroid>> = database.asteroidDao.getWeekAsteroids().map {
        it.asDomainModel()
    }

    val todayAsteroids: LiveData<List<Asteroid>> = database.asteroidDao.getTodayAsteroids().map {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(): Pair<AsteroidsApiStatus, AsteroidFilter> {
        return withContext(Dispatchers.IO) {
            try {
                val response = AsteroidApi.asteroidsService.getAsteroidsList().execute()

                if (response.isSuccessful) {
                    val jsonString = response.body()
                    if (jsonString != null) {
                        val asteroidList = parseAsteroidsJsonResult(JSONObject(jsonString))
                        database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
                        return@withContext Pair(AsteroidsApiStatus.SUCCESS, AsteroidFilter.WEEK)
                    }
                }

                return@withContext Pair(AsteroidsApiStatus.ERROR, AsteroidFilter.WEEK)
            } catch (e: Exception) {
                return@withContext Pair(AsteroidsApiStatus.ERROR, AsteroidFilter.WEEK)
            }
        }
    }

    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroids()
        }
    }
}

/** I do not know why but I couldn't use Transformations so I create my own function :) */
fun <T, R> LiveData<T>.map(transform: (T) -> R): LiveData<R> {
    val mediatorLiveData = MediatorLiveData<R>()
    mediatorLiveData.addSource(this) { sourceData ->
        mediatorLiveData.value = transform(sourceData)
    }
    return mediatorLiveData
}
