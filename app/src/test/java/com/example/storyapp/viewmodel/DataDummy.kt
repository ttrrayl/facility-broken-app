package com.example.storyapp.viewmodel

import com.example.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStory() : List<ListStoryItem> {
        val storyList: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://th.bing.com/th/id/OIP.1MZ3IQ50dBysLAdeCnt5wwHaHa?w=172&h=180&c=7&r=0&o=5&pid=1.7",
                "2022-02-22T22:22:22Z",
                "NIAA",
            "ini story saya",
                0.0,
                "$i",
                1.0
            )
            storyList.add(story)
        }
        return storyList
    }
}