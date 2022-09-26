package com.toharifqi.instageram.common

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.toharifqi.instageram.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun ImageView.setImageFromUrl(context: Context, photoUrl: String) {
    Glide
        .with(context)
        .load(photoUrl)
        .placeholder(R.drawable.photo_placeholder)
        .error(R.drawable.photo_placeholder)
        .into(this)
}

fun TextView.setFormattedDate(context: Context, timestamp: String) {
    val date = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US)
        .parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
    this.text = context.getString(R.string.text_posted_at, formattedDate)
}