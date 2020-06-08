package com.morgan.trailers.data.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.morgan.trailers.data.models.Movie
import com.morgan.trailers.data.models.MovieType
import kotlinx.coroutines.flow.Flow



@Dao
interface MovieDao{
    // ORDER BY releaseDate DESC
    @Query("SELECT * FROM movies WHERE movieType = :movieType ORDER BY releaseDate DESC")
    fun getMovies(movieType: MovieType): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movies WHERE (originalTitle LIKE :queryString) ")
    fun searchMovies(queryString: String): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1" )
    suspend fun getMovieById(id: Int): Movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movie: List<Movie>)
}