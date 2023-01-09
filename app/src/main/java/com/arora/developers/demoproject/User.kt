package com.arora.developers.demoproject

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user_table" ,indices = [Index(value = ["fsEmail"], unique = true)]) // User Entity represents a table within the database.
data class User(
    @PrimaryKey(autoGenerate = true)
    val fiId: Int, // <- 'id' is the primary key which will be autogenerated by the Room library.
    val fsFirstName: String,
    val fsLastName: String,
    val fsDob: String,
    val fsEmail: String,
    val fsPassword: String
    ): Parcelable