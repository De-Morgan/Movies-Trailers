package com.morgan.trailers.data.models




data class MovieVideo(
    val id : String,
    val key: String,
    val  name: String,
    val  size: Int,
    val  site: String,
    val type: String
){
    val  movieUrl: String
        get() =
            if(site == "YouTube") "https://www.youtube.com/watch?v=$key"
            else "https://vimeo.com/$key"

}

data class MovieVideoResponse(
    val  id: String,
    val  results: List<MovieVideo> = emptyList()
)