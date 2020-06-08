package com.morgan.trailers.data.remote

import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.data.models.MovieType
import retrofit2.http.Query
import timber.log.Timber

suspend fun searchMovie(query: String,
                        service: NetworkApiService,
                        page: Int = 1,
                        onSuccess: suspend (movies: List<Movie>)->Unit,
                        onError: (error: String)->Unit
){
    Timber.d("query: $query, page: $page")
    try {
        val response =   service.movieApiService.searchMovies(query,page)
        if(response.isSuccessful){
            Timber.d("got a response: $response")
            response.body()?.let {
                onSuccess(it.results)
            }
        }else{
            Timber.d("failed to get a response: $response")
            onError(response.errorBody().toString() ?: "Unknown error")
        }
    }catch (err: Throwable){
        Timber.d("failed to get a response with error $err")
        onError(err.message ?: "unknown error")
    }
}

suspend fun getMovieFromType(movieType: MovieType,
                             service: NetworkApiService,
                             page: Int = 1,
                             onSuccess: suspend (movies: List<Movie>)->Unit,
                             onError: (error: String)->Unit
){
    Timber.d("query: $movieType, page: $page")
    try {
        val response =   service.movieApiService.getMovies(movieType.type,page)
        if(response.isSuccessful){
            Timber.d("got a response: $response")
            response.body()?.let {
                onSuccess(it.results)
            }
        }else{
            Timber.d("failed to get a response: $response")
            onError(response.errorBody().toString() ?: "Unknown error")
        }
    }catch (err: Throwable){
        Timber.d("failed to get a response with error $err")
        onError(err.message ?: "unknown error")
    }
}