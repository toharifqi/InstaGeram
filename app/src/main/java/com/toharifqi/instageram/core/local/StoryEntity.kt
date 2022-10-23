package com.toharifqi.instageram.core.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toharifqi.instageram.core.remote.StoryResponse

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "lat")
    val lat: Double?,

    @ColumnInfo(name = "lng")
    val lng: Double?
) {
    constructor(response: StoryResponse) : this(
        id = response.id,
        name = response.name,
        description = response.description,
        photoUrl = response.photoUrl,
        createdAt = response.createdAt,
        lat = response.lat,
        lng = response.lon
    )
}
