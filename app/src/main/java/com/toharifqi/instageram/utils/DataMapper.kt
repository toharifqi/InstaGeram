package com.toharifqi.instageram.utils

import com.toharifqi.instageram.core.remote.StoryResponse
import com.toharifqi.instageram.storylist.StoryDomainData

object DataMapper {
    fun mapStoryResponsesToDomain(input: List<StoryResponse>): List<StoryDomainData> {
        return input.map {
            StoryDomainData(it)
        }
    }
}
