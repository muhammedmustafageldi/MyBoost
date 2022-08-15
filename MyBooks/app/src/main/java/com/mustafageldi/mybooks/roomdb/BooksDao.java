package com.mustafageldi.mybooks.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.mustafageldi.mybooks.model.FinishedBooks;
import com.mustafageldi.mybooks.model.NowRead;
import com.mustafageldi.mybooks.model.RequestedBooks;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface BooksDao {


    //QUERY'S
    @Query("SELECT * FROM NowRead")
    Flowable<List<NowRead>> getAllNowRead();

    @Query("SELECT * FROM FinishedBooks")
    Flowable<List<FinishedBooks>> getAllFinishedBooks();

    @Query("SELECT * FROM RequestedBooks")
    Flowable<List<RequestedBooks>> getAllRequestedBooks();

    //SINGLE QUERY'S
    @Query("SELECT * FROM NowRead WHERE id = :id")
    Flowable<NowRead> getSingleNowRead(int id);

    @Query("SELECT * FROM FinishedBooks WHERE id = :id")
    Flowable<FinishedBooks> getSingleFinishedBooks(int id);

    @Query("SELECT * FROM RequestedBooks WHERE id = :id")
    Flowable<RequestedBooks> getSingleRequestedBooks(int id);


    //INSERT TRANSACTIONS
    @Insert
    Completable insertNowRead(NowRead nowRead);

    @Insert
    Completable insertFinishedBook(FinishedBooks finishedBooks);

    @Insert
    Completable insertRequestedBooks(RequestedBooks requestedBooks);


    //DELETE TRANSACTIONS
    @Delete
    Completable deleteNowRead(NowRead nowRead);

    @Update
    Completable update(NowRead nowRead);

    @Query("UPDATE NowRead SET readPage = :readPage WHERE id =:id")
    Completable updateNowRead(String readPage, int id);

    @Query("DELETE FROM RequestedBooks WHERE id = :id")
    Completable deleteByIdRequested(int id);

    @Query("DELETE FROM NowRead WHERE id = :id")
    Completable deleteByNowRead(int id);


}
