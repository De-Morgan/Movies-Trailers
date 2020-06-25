package com.morgan.trailers.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.paging.PagedList
import com.morgan.trailers.data.local.MovieDao
import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.data.models.MovieType
import com.morgan.trailers.data.remote.NetworkApiService
import com.morgan.trailers.data.remote.getMovieFromType
import com.morgan.trailers.data.remote.searchMovie
import kotlinx.coroutines.*
import timber.log.Timber


class MovieBoundaryCallback (
    private val movieType: MovieType ,
     private  val service: NetworkApiService,
    private val movieDao: MovieDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
): PagedList.BoundaryCallback<Movie>(){

    private var lastRequestedPage = 1

    private var isRequestInProgress = false
    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors


    private val _loadingProgress = MutableLiveData<Boolean>()
    val loadingProgress: LiveData<Boolean>
        get() = _loadingProgress

    override fun onZeroItemsLoaded() {
        Timber.d("onZeroItemsLoaded called")
        val  scope = CoroutineScope(dispatcher)
        scope.launch {
            requestAndSaveMovie(movieType)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        Timber.d("onItemAtEndLoaded called")

        val  scope = CoroutineScope(dispatcher)
        scope.launch {
            requestAndSaveMovie(movieType)
        }    }

    private suspend fun requestAndSaveMovie(movieType: MovieType = MovieType.Popular){
        if (isRequestInProgress) return
        isRequestInProgress = true
        _loadingProgress.postValue(true)
        getMovieFromType(movieType, service, lastRequestedPage, { movies->
            movies.forEach {
                it.movieType = movieType
            }
            movieDao.insertMovies(movies)
            lastRequestedPage++
            isRequestInProgress = false
            _loadingProgress.postValue(false)

        }, {error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
            _loadingProgress.postValue(false)

        })

    }





}

class MovieSearchBoundaryCallback (
    private val query: String,
   private val service: NetworkApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
            private val movieDao: MovieDao
): PagedList.BoundaryCallback<Movie>(){

    private var searchRequestedPage = 1

    private var isRequestInProgress = false
    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    private val _loadingProgress = MutableLiveData<Boolean>()
    val loadingProgress: LiveData<Boolean> get() = _loadingProgress
    override fun onZeroItemsLoaded() {
        val  scope = CoroutineScope(dispatcher)
        scope.launch {
            searchAndSaveMovie(query)
        }    }

    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        val  scope = CoroutineScope(dispatcher)
        scope.launch {
            searchAndSaveMovie(query)
        }     }

    private suspend fun searchAndSaveMovie(query: String){
        if (isRequestInProgress) return
        isRequestInProgress = true
        _loadingProgress.postValue(true)

        searchMovie(query, service, searchRequestedPage, { movies->
            searchRequestedPage++
            isRequestInProgress = false
            _loadingProgress.postValue(false)

        }, {error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
            _loadingProgress.postValue(false)

        })

    }
}