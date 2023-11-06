package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

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

    init {
        getASteroidsList()
        _filter.value = AsteroidFilter.TODAY
    }

    private fun getASteroidsList() {
        _status.value = AsteroidsApiStatus.LOADING
        viewModelScope.launch {
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
