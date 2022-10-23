package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.remote.StoryResponse
import android.os.Parcelable
import com.toharifqi.instageram.core.local.StoryEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryDomainData(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lng: Double?
) : Parcelable {
    constructor(response: StoryResponse) : this(
        id = response.id,
        name = response.name,
        description = response.description,
        photoUrl = response.photoUrl,
        createdAt = response.createdAt,
        lat = response.lat,
        lng = response.lon
    )

    constructor(storyEntity: StoryEntity) : this(
        id = storyEntity.id,
        name = storyEntity.name,
        description = storyEntity.description,
        photoUrl = storyEntity.photoUrl,
        createdAt = storyEntity.createdAt,
        lat = storyEntity.lat,
        lng = storyEntity.lng
    )
}
