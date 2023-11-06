package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.util.Constants.API_KEY
import com.udacity.asteroidradar.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private fun getStartDate(): String {
    val actualDate = Calendar.getInstance()
    actualDate.add(Calendar.DAY_OF_MONTH, -7)
    val endDate = actualDate.time
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formattedDate.format(endDate)
}

private fun getEndDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

interface AsteroidsService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidsList(
        @Query("start_date") startDate: String = getStartDate(),
        @Query("end_date") endDate: String = getEndDate(),
        @Query("api_key") apiKey: String = API_KEY,
    ): Call<String>
}

private val retrofit = Retrofit.Builder()
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

object Network {
    val retrofitService: AsteroidsService = retrofit.create(AsteroidsService::class.java)
}
