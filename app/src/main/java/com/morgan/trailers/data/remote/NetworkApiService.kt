package com.morgan.trailers.data.remote

import com.morgan.trailers.BuildConfig
import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.data.models.MovieType
import com.morgan.trailers.data.models.MovieVideoResponse
import com.morgan.trailers.data.models.ResultResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
private const val apiKey: String = BuildConfig.API_KEY


object NetworkApiService {
   private const val BASE_URL = "https://api.themoviedb.org/3/"
     const val IMAGE_BASE_URL: String = "https://image.tmdb.org/t/p/w185"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val movieApiService = retrofit.create(MovieApiService::class.java)

}



interface MovieApiService{
    @GET("movie/{type}?api_key=$apiKey")
    suspend fun getMovies(@Path("type") type: String, @Query("page")page: Int = 1 ): Response<ResultResponse<Movie>>

    @GET("search/movie?api_key=$apiKey")
    suspend fun searchMovies(@Query("query")query:String, @Query("page") page: Int = 1, @Query("include_adult") includeAdult :Boolean = false): Response<ResultResponse<Movie>>

    @GET("movie/{movie_id}/videos?api_key=$apiKey")
    suspend fun getMovieVideo(@Path("movie_id") movieId: Int ): Response<MovieVideoResponse>

}

