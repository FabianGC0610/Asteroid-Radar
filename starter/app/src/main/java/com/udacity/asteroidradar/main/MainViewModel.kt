package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class AsteroidsApiStatus { SUCCESS, ERROR, LOADING }

enum class AsteroidFilter { WEEK, SAVED, TODAY }

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _filter = MutableLiveData<AsteroidFilter>()

    private val _status = MutableLiveData<AsteroidsApiStatus>()
    val status: LiveData<AsteroidsApiStatus> get() = _status

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails: LiveData<Asteroid?> get() = _navigateToAsteroidDetails

    init {
        getASteroidsList()
        _filter.value = AsteroidFilter.WEEK
    }

    private fun getASteroidsList() {
        _status.value = AsteroidsApiStatus.LOADING
        Network.retrofitService.getAsteroidsList().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                response.body()?.let {
                    _status.value = AsteroidsApiStatus.SUCCESS
                    _filter.value = AsteroidFilter.WEEK
                    val asteroidList = parseAsteroidsJsonResult(JSONObject(it))
                    viewModelScope.launch {
                        asteroidsRepository.refreshAsteroids(asteroidList)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _status.value = AsteroidsApiStatus.ERROR
            }
        })
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
