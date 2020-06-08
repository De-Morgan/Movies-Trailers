package com.morgan.trailers.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.morgan.trailers.data.remote.NetworkApiService
import timber.log.Timber


@Entity(tableName = "movies")
data class Movie(
    val popularity: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val video: Boolean,
    @SerializedName("poster_path")
    val posterPath: String?,
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val overview:String,
    @SerializedName("release_date")
    val releaseDate: String,
   @SerializedName("genre_ids")
   val genreIds: List<Int>,
    var movieType: MovieType = MovieType.Popular
){
    val  posterUrl: String?
    get() {
       return "${NetworkApiService.IMAGE_BASE_URL}$posterPath"
    }
    val backdropUrl: String?
    get(){
        return "${NetworkApiService.IMAGE_BASE_URL}$backdropPath"
    }
    val releaseYear: String
    get() = releaseDate.split("-")[0]

}