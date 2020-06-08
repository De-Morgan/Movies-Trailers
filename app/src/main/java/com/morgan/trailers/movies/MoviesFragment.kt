package com.morgan.trailers.movies

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.morgan.trailers.R

import com.morgan.trailers.TrailersApplication
import com.morgan.trailers.data.models.MovieType
import com.morgan.trailers.databinding.FragmentMoviesBinding
import com.morgan.trailers.utils.EventObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class MoviesFragment : Fragment() {

    private val  movieViewModel by viewModels<MoviesViewModel> {
        MovieViewModelFactory((requireActivity().applicationContext as TrailersApplication).movieRepository)
    }

    private lateinit var binding: FragmentMoviesBinding
    private  lateinit var adapter: MovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = MovieAdapter(movieViewModel)
        binding = FragmentMoviesBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = movieViewModel
            recyclerView.adapter = adapter
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpMovieObserver()
        setUpNetworkErrorObserver()
        setUpNavigationToDetailsObserver()
        setUpTitleObserver()
    }


    private fun setUpMovieObserver(){
           movieViewModel.movies.observe(viewLifecycleOwner, Observer {
               Timber.d("This is the number of  movies observed${it.size}")
      adapter.submitList(it)
      })
    }

    private fun  setUpNetworkErrorObserver(){

        movieViewModel.networkErrors.observe(viewLifecycleOwner, Observer{
            Snackbar.make(requireView(),it,Snackbar.LENGTH_LONG).show()
        })
    }

    private fun setUpTitleObserver(){
        movieViewModel.currentTitle.observe(viewLifecycleOwner, Observer {
            requireActivity().setTitle(it)
        })
    }

    private fun setUpNavigationToDetailsObserver(){
        movieViewModel.openDetailsEvent.observe(viewLifecycleOwner, EventObserver{
            openMovieDetail(it)
        })
    }

    private fun openMovieDetail(movieId: Int){
        Timber.d("openMovieDetail is called with $movieId in MoviesFragment")

        val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetail(movieId)
        findNavController().navigate(action)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_type_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){

        R.id.popular_menu->{
            movieViewModel.changeMovieType(MovieType.Popular)
            true
        }
        R.id.top_rated_menu->{
            movieViewModel.changeMovieType(MovieType.TopRated)
            true
        }
        R.id.now_playing_menu->{
            movieViewModel.changeMovieType(MovieType.NowPlaying)
            true
        }
        else ->  {
            movieViewModel.changeMovieType(MovieType.UpComing)
            true
        }
    }
}
