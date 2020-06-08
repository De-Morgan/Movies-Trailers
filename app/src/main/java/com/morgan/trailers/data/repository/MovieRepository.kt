package com.morgan.trailers.data.repository

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.morgan.trailers.data.local.MovieDao
import com.morgan.trailers.data.models.*
import com.morgan.trailers.data.remote.NetworkApiService

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.http.Query
import timber.log.Timber


class MovieRepository (
    val network: NetworkApiService,
    val  movieDao: MovieDao
){

    suspend fun getMovieById(movieId: Int): Movie = movieDao.getMovieById(movieId)

    fun searchMovie(query: String) : MovieResult {
        Timber.d("New query $query")
        val dataSourceFactory = movieDao.searchMovies(query)

        val boundaryCallback = MovieSearchBoundaryCallback(query,network,movieDao = movieDao)
        val  networkErrors = boundaryCallback.networkErrors
        val  loadingProgress = boundaryCallback.loadingProgress

        val  data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback).build()
        return MovieResult(data,networkErrors,loadingProgress)
    }

    fun  getMovieFromType(movieType: MovieType): MovieResult{
        Timber.d("New query $movieType")
        val dataSourceFactory = movieDao.getMovies(movieType)

        val boundaryCallback = MovieBoundaryCallback(movieType,network,movieDao = movieDao)
        val  networkErrors = boundaryCallback.networkErrors
        val  loadingProgress = boundaryCallback.loadingProgress
        val pageConfig = PagedList.Config.Builder()
            .setPrefetchDistance(0)
            .setPageSize(DATABASE_PAGE_SIZE)
            .setInitialLoadSizeHint(DATABASE_PAGE_SIZE).build()
        val  data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback).build()
        return MovieResult(data,networkErrors,loadingProgress)
    }

    suspend  fun refreshPopularMovies(){
       val result =  network.movieApiService.getMovies(MovieType.Popular.type)

        try {
            if (result.isSuccessful){
                result.body()?.results?.let {
                    it.forEach { movie->
                        movie.movieType = MovieType.Popular
                    }
                    movieDao.insertMovies(it)
                }
            }
        }catch (error: Throwable){
            throw  MovieRefreshError("Unable to fetch movies",error)
        }

    }

    suspend fun getMovieVideo(movieId: Int):MovieVideo?{
        val movieVideoResponse = network.movieApiService.getMovieVideo(movieId)
        Timber.d("This is the movieVideoResponse $movieVideoResponse")
        if (movieVideoResponse.isSuccessful){
            movieVideoResponse.body()?.let {
               if(!it.results.isNullOrEmpty()){
                   return it.results[0]
               }
            }
        }
        return  null
    }


    companion object{
        private const val DATABASE_PAGE_SIZE = 50
    }
}

