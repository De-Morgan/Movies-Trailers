package com.morgan.trailers.movie_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.morgan.trailers.TrailersApplication
import com.morgan.trailers.databinding.FragmentMovieDetailBinding
import com.morgan.trailers.movies.MoviesFragmentDirections
import com.morgan.trailers.utils.EventObserver
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class MovieDetailFragment : Fragment() {


    private val movieDetailViewModel by viewModels<MovieDetailViewModel> {
        MovieDetailViewModelFactory((requireActivity().applicationContext as TrailersApplication).movieRepository)
    }
    private val args: MovieDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentMovieDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = movieDetailViewModel
        }


        movieDetailViewModel.acceptMovieId(args.movieId)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpTitleObserver()
        setUpNavigationToVideoPlayerObserver()
    }

    private fun setUpTitleObserver(){
        movieDetailViewModel.currentTitle.observe(viewLifecycleOwner, Observer {
            requireActivity().title = it
        })
    }



    private fun setUpNavigationToVideoPlayerObserver(){
        movieDetailViewModel.openVideoPlayerEvent.observe(viewLifecycleOwner, EventObserver{
            Timber.d(" setUpNavigationToVideoPlayerObserver called with $it")
            openWebPage(it)
        })
    }



    private  fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

}
