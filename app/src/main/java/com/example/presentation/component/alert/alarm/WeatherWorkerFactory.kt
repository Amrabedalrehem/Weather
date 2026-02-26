package com.example.presentation.component.alert.alarm


import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.data.IRepository
import com.example.data.Repository

class WeatherWorkerFactory(private val repository: IRepository) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
                WeatherNotificationWorker::class.java.name -> WeatherNotificationWorker(appContext, workerParameters, repository)
                else -> null
            }
        }
    }