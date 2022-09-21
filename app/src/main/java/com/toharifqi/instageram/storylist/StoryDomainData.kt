package com.toharifqi.instageram.storylist

import com.toharifqi.instageram.core.remote.StoryResponse

data class StoryDomainData(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String
) {
    constructor(response: StoryResponse) : this(
        id = response.id,
        name = response.name,
        description = response.description,
        photoUrl = response.photoUrl,
        createdAt = response.createdAt
    )
}
