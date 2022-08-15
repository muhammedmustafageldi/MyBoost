package com.mustafageldi.mybooks.roomdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.model.RequestedBooks;

@Database(entities = {NowRead.class, FinishedBooks.class, RequestedBooks.class},version = 5)
public abstract class BooksDatabase extends RoomDatabase {
    public abstract BooksDao booksDao();
}
