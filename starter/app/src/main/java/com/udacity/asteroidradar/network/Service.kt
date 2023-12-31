package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.util.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.util.Constants.BASE_URL
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object RequestLogic {
    private var isTodayRequired: Boolean = false

    fun setTodayRequired(value: Boolean) {
        isTodayRequired = value
    }

    fun getTodayRequired(): Boolean {
        return isTodayRequired
    }
}

private fun getEndDate(): String {
    val actualDate = Calendar.getInstance()
    actualDate.add(Calendar.DAY_OF_MONTH, +7)
    val endDate = actualDate.time
    val formattedDate = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
    return formattedDate.format(endDate)
}

private fun getStartDate(): String {
    return if (RequestLogic.getTodayRequired()) {
        SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault()).format(Date())
    } else {
        val actualDate = Calendar.getInstance()
        actualDate.add(Calendar.DAY_OF_MONTH, +1)
        val endDate = actualDate.time
        val formattedDate = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDate.format(endDate)
    }
}

interface AsteroidsService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidsList(
        @Query("start_date") startDate: String = getStartDate(),
        @Query("end_date") endDate: String = getEndDate(),
        @Query("api_key") apiKey: String = BuildConfig.ASTEROIDS_API_KEY,
    ): Call<String>

    @GET("planetary/apod")
    fun getPictureOfDay(
        @Query("api_key") apiKey: String = BuildConfig.ASTEROIDS_API_KEY,
    ): Deferred<PictureOfDay>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val pictureOfDayRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

private val asteroidsRetrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .client(
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build(),
    )
    .build()

object AsteroidApi {
    val asteroidsService: AsteroidsService = asteroidsRetrofit.create(AsteroidsService::class.java)
    val pictureOfDayService: AsteroidsService = pictureOfDayRetrofit.create(AsteroidsService::class.java)
}
