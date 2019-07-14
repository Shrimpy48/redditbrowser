package com.example.redditbrowser.utils

import android.app.Application
import com.example.redditbrowser.database.PostDatabase
import com.example.redditbrowser.repositories.PostRepository
import java.util.concurrent.Executors

class ServiceProvider(val app: Application, private val inMemory: Boolean) {

    companion object {
        private val lock = Any()
        private var instance: ServiceProvider? = null

        fun instance(app: Application, inMemory: Boolean): ServiceProvider {
            synchronized(lock) {
                if (instance == null) {
                    instance = ServiceProvider(app, inMemory)
                }
                return instance!!
            }
        }
    }

    private val executor = Executors.newSingleThreadExecutor()

    private val db by lazy {
        PostDatabase.create(app, inMemory)
    }

    fun getRepository(): PostRepository {
        return PostRepository(db, executor)
    }
}
