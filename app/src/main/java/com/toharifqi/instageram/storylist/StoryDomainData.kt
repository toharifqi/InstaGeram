package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.remote.StoryResponse
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryDomainData(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String
) : Parcelable {
    constructor(response: StoryResponse) : this(
        id = response.id,
        name = response.name,
        description = response.description,
        photoUrl = response.photoUrl,
        createdAt = response.createdAt
    )
}
