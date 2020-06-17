package com.morgan.trailers.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.databinding.MovieItemBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
class MovieAdapter(private val moviesViewModel: MoviesViewModel): PagedListAdapter<Movie,MovieAdapter.ViewHolder>(MovieDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.fromParent(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            holder.bind(it,moviesViewModel)
        }
    }


    class ViewHolder private constructor(val movieItemBinding: MovieItemBinding): RecyclerView.ViewHolder(movieItemBinding.root) {
        companion object{
            fun fromParent(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MovieItemBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

        fun bind(movie: Movie, moviesViewModel: MoviesViewModel){
            movieItemBinding.movie = movie
            movieItemBinding.viewmodel = moviesViewModel
            movieItemBinding.executePendingBindings()
        }
    }


}



object MovieDiffCallback: DiffUtil.ItemCallback<Movie>(){
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id ==newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem ==newItem
    }

}