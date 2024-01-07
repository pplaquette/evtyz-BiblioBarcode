package com.evanzheng.bibliobarcode

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Book::class], version = 1, exportSchema = false)

abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao?
}