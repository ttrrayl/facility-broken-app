package com.example.kumandraPJ.viewmodel

import com.example.kumandraPJ.data.remote.response.Report

object DataDummy {
    fun generateDummyStory() : List<Report> {
        val storyList: MutableList<Report> = arrayListOf()
        for (i in 0..10) {
            val story = Report(
                "2022-02-22T22:22:22Z",
                "ini story saya",
                "NIAA",
                "https://th.bing.com/th/id/OIP.1MZ3IQ50dBysLAdeCnt5wwHaHa?w=172&h=180&c=7&r=0&o=5&pid=1.7",
            "1",
                "1",
                "1",
                "1",
                "12",
                "3",
                "4",
                "5",
                "baru",
                "2022-02-22T22:22:22Z"

            )
            storyList.add(story)
        }
        return storyList
    }
}