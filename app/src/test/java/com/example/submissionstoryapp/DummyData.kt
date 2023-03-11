package com.example.submissionstoryapp

import com.example.submissionstoryapp.database.StoryEntity
import com.example.submissionstoryapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DummyData {

    fun generateStoryResponse(): ResponseStory {
        val error = false
        val msg = "Success Get Data"
        val list = mutableListOf<ListStory>()

        for (i in 0 until 5) {
            val data = ListStory(
                "id-$i",
                "name-$i",
                "description-$i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                -6.160528597476866,
                106.78740077120638
            )
            list.add(data)
        }
        return ResponseStory(error, msg, list)
    }

    fun generateListStories(): List<StoryEntity> {
        val data = arrayListOf<StoryEntity>()

        for (i in 0 until 5) {
            val item = StoryEntity(
                "id-$i",
                "name-$i",
                "description-$i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                -6.160528597476866,
                106.78740077120638
            )
            data.add(item)
        }
        return data
    }

    fun generateLogin(): LoginResponse {
        val login = ResultLogin(
            userId = "1234",
            name = "Zaky Raihan",
            token = "token-token"
        )
        return LoginResponse(result = login, error = false, message = "Sukses")
    }

    fun generateRegister(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "sukses"
        )
    }

    fun generateFileUploadResponse(): UploadStoryResponse {
        return UploadStoryResponse(
            error = false,
            message = "sukses"
        )
    }

    fun generateRequestBody(): String {
        return "text"
    }

    fun generateMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateToken(): String{
        return "token-token"
    }

}