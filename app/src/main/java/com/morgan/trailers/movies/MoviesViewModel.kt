package com.morgan.trailers.movies

import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.morgan.trailers.R
import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.data.models.MovieResult
import com.morgan.trailers.data.models.MovieType
import com.morgan.trailers.data.repository.MovieRepository
import com.morgan.trailers.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@FlowPreview
@ExperimentalCoroutinesApi
class MoviesViewModel(private val movieRepository: MovieRepository): ViewModel(){


    private val movieTypeSelected = MutableLiveData<MovieType>(MovieType.Popular)
    private val queryLiveData = MutableLiveData<String>()

    private val movieResult : LiveData<MovieResult> = Transformations.map(movieTypeSelected){
            movieType ->
        movieRepository.getMovieFromType(movieType)
    }
    private val movieSearchResult : LiveData<MovieResult> = Transformations.map(queryLiveData){
            query ->
        movieRepository.searchMovie(query)
    }

    val  movies: LiveData<PagedList<Movie>> = Transformations.switchMap(movieResult){it.data}
    val  networkErrors: LiveData<String> = Transformations.switchMap(movieResult){it.networkErrors}
    val loadingSpinner: LiveData<Boolean> = Transformations.switchMap(movieResult){it.loadingData}

    private val _currentTitle = MutableLiveData<Int>()
    val currentTitle: LiveData<Int> = _currentTitle


//    private val _networkError =  MutableLiveData<Event<String>>();
//    val networkError: LiveData<Event<String>>
//    get ()= _networkError


    private val _openDetailsEvent = MutableLiveData<Event<Int>>()
    val openDetailsEvent: LiveData<Event<Int>> = _openDetailsEvent


    init {
    setTitle(R.string.popular_title)
}




    fun changeMovieType(movieType: MovieType){
        movieTypeSelected.postValue(movieType)
        when(movieType){
            MovieType.Popular -> setTitle(R.string.popular_title)
            MovieType.TopRated -> setTitle(R.string.top_rated_title)
            MovieType.NowPlaying -> setTitle(R.string.now_playing_title)
            MovieType.UpComing -> setTitle(R.string.upcoming_title)
        }

    }

    fun openMovieDetails(id: Int){
        _openDetailsEvent.value = Event(id)
        Timber.d("openMovieDetails is called with $id")
    }


    private fun  setTitle(@StringRes titleString: Int){
        _currentTitle.value = titleString
    }


}

@Suppress("UNCHECKED_CAST")
class MovieViewModelFactory (
    private val movieRepository: MovieRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (MoviesViewModel(movieRepository) as T)
}