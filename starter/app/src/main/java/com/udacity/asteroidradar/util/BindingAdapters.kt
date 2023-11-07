package com.udacity.asteroidradar.util

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.main.AsteroidsApiStatus
import com.udacity.asteroidradar.main.MainAdapter

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as MainAdapter
    adapter.submitList(data)
}

@BindingAdapter("listLogic")
fun showRecyclerView(recyclerView: RecyclerView, status: AsteroidsApiStatus) {
    recyclerView.isVisible = status != AsteroidsApiStatus.LOADING
}

@BindingAdapter("logic")
fun showProgressBar(progressBar: ProgressBar, status: AsteroidsApiStatus) {
    if (status == AsteroidsApiStatus.LOADING) {
        progressBar.isVisible = true
        progressBar.contentDescription =
            progressBar.context.getString(R.string.loading_progress_bar_content_description)
    } else {
        progressBar.isVisible = false
    }
}

@BindingAdapter("pictureOfDayImage")
fun bindPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    if (pictureOfDay != null) {
        if (pictureOfDay.mediaType == "image") {
            Picasso.Builder(imageView.context).build()
                .load(pictureOfDay.url)
                .into(imageView)
            imageView.contentDescription = imageView.context.getString(
                R.string.nasa_picture_of_day_content_description_format,
                pictureOfDay.title,
            )
        } else {
            imageView.setImageResource(R.drawable.asteroid_safe)
            imageView.contentDescription = imageView.context.getString(
                R.string.picture_of_day_no_image_found_content_description,
            )
        }
    } else {
        imageView.contentDescription = imageView.context.getString(
            R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet,
        )
    }
}

@BindingAdapter("detailsImageContentDescription")
fun showDetailsImageContentDescription(imageView: ImageView, isPotentiallyHazardous: Boolean) {
    imageView.contentDescription = if (isPotentiallyHazardous) {
        imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("statusIconContentDescription")
fun showStatusIconContentDescription(imageView: ImageView, isPotentiallyHazardous: Boolean) {
    imageView.contentDescription = if (isPotentiallyHazardous) {
        imageView.context.getString(R.string.potentially_hazardous_asteroid_icon)
    } else {
        imageView.context.getString(R.string.not_hazardous_asteroid_icon)
    }
}
