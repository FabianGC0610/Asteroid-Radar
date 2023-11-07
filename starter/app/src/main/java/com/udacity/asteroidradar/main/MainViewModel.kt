package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.RequestLogic
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.lang.Exception

enum class AsteroidsApiStatus { SUCCESS, ERROR, LOADING }

enum class AsteroidFilter { WEEK, SAVED, TODAY }

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _filter = MutableLiveData<AsteroidFilter?>()

    private val _status = MutableLiveData<AsteroidsApiStatus?>()
    val status: LiveData<AsteroidsApiStatus?> get() = _status

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails: LiveData<Asteroid?> get() = _navigateToAsteroidDetails

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?> get() = _pictureOfDay

    init {
        getPictureOfDay()
        getASteroidsList()
        _filter.value = AsteroidFilter.WEEK
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                val pictureOfDay = AsteroidApi.pictureOfDayService.getPictureOfDay().await()
                _pictureOfDay.value = pictureOfDay
            } catch (e: Exception) {
                _pictureOfDay.value = null
            }
        }
    }

    private fun getASteroidsList() {
        _status.value = AsteroidsApiStatus.LOADING
        viewModelScope.launch {
            RequestLogic.setTodayRequired(true)
            val pair = asteroidsRepository.refreshAsteroids()
            _status.value = pair.first
            _filter.value = pair.second
        }
    }

    val asteroidsList: LiveData<List<Asteroid>> = _filter.switchMap { filter ->
        when (filter!!) {
            AsteroidFilter.SAVED -> asteroidsRepository.savedAsteroids
            AsteroidFilter.WEEK -> asteroidsRepository.weekAsteroids
            AsteroidFilter.TODAY -> asteroidsRepository.todayAsteroids
        }
    }

    fun changeFilter(filter: AsteroidFilter) {
        _filter.value = filter
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    fun onAsteroidClickedComplete() {
        _navigateToAsteroidDetails.value = null
    }
}
