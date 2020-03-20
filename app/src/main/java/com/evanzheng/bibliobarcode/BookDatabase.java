package com.evanzheng.bibliobarcode;

import androidx.room.RoomDatabase;
import androidx.room.Database;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
}
