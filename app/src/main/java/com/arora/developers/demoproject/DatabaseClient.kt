package com.arora.developers.demoproject

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database

import androidx.room.RoomDatabase

class DatabaseClient private constructor(private val mCtx: Context) {
    //our app database object
   /* private val mcUserDatabase: UserDatabase*/
   /* fun getUserDatabase(): UserDatabase {
        return mcUserDatabase
    }*/

    companion object {
        private var mInstance: UserDatabase? = null

        @Synchronized
        fun getInstance(mCtx: Context): UserDatabase {
            if (mInstance == null) {
                mInstance  = Room.databaseBuilder(
                        mCtx.applicationContext,
                UserDatabase::class.java, "db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return mInstance!!
        }

    }


    init {
        //creating the app database with Room database builder
        //db is the name of the database

    }
}