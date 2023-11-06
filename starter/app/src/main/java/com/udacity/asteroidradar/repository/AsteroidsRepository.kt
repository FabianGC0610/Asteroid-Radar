package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun refreshAsteroids(asteroidList: ArrayList<Asteroid>) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
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
