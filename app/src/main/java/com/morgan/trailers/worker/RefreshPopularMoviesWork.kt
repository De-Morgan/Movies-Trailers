package com.morgan.trailers.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.morgan.trailers.R
import com.morgan.trailers.TrailersApplication
import com.morgan.trailers.data.models.MovieRefreshError
import com.morgan.trailers.data.remote.NetworkApiService
import com.morgan.trailers.data.repository.MovieRepository
import com.morgan.trailers.utils.sendNotification

class RefreshPopularMoviesWork(val context: Context,params: WorkerParameters, private val movieRepository: MovieRepository):CoroutineWorker(context,params){

    private  val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager

    override suspend fun doWork(): Result {

        return  try {
            movieRepository.refreshPopularMovies()

            notificationManager.sendNotification(
                context.getText(R.string.movie_fresh_content).toString(),
                context
            )
            Result.success()
        }catch ( error: MovieRefreshError){
            Result.failure()
        }
    }

    class Factory():WorkerFactory(){
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            val movieRepository = (appContext as TrailersApplication).movieRepository
            return RefreshPopularMoviesWork(appContext,workerParameters,movieRepository)
        }
    }
}