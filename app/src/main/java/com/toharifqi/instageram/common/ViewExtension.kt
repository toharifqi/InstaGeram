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

fun ImageView.setImageFromUrl(context: Context, photoUrl: String) {
    Glide
        .with(context)
        .load(photoUrl)
        .placeholder(R.drawable.image_placeholder)
        .error(R.drawable.image_placeholder)
        .into(this)
}

fun TextView.setFormattedDate(context: Context, timestamp: String) {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        .parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
    this.text = context.getString(R.string.text_posted_at, formattedDate)
}
