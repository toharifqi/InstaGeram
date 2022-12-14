package com.toharifqi.instageram

import com.toharifqi.instageram.core.local.StoryEntity
import com.toharifqi.instageram.core.remote.StoryResponse
import com.toharifqi.instageram.storylist.StoryDomainData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {

    fun generateDummyListStoryResponse(): List<StoryResponse> {
        val stories = mutableListOf<StoryResponse>()

        for (i in 0..10) {
            val story = StoryResponse(
                id = "story-aZ24q3-P41-OQKtp$i",
                name = "Rifqi Naufal",
                description = "coba kali",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1666490597878_Lt6H7nnD.jpg",
                createdAt = "2022-10-23T02:03:17.881Z",
                lat = -8.1706731,
                lon = 112.2078284
            )
            stories.add(story)
        }

        return stories
    }

    fun generateDummyListStoryEntity(): List<StoryEntity> {
        return generateDummyListStoryResponse().map { StoryEntity(it) }
    }

    fun generateDummyListStoryDomainData(): List<StoryDomainData> {
        return generateDummyListStoryEntity().map { StoryDomainData(it) }
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }
}
