package com.mustafageldi.mybooks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class NowRead {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="bookName")
    public String name;

    @ColumnInfo(name="author")
    public String author;

    @ColumnInfo(name="numberOfPages")
    public String numberOfPages;

    @ColumnInfo(name="readPage")
    public String readPage;

    @ColumnInfo(name = "timeRead")
    public String timeRead;

    @ColumnInfo(name = "image")
    public byte[] image;

    public NowRead(String name,String author,String numberOfPages,String readPage,String timeRead, byte[] image){
        this.name = name;
        this.author = author;
        this.numberOfPages = numberOfPages;
        this.readPage = readPage;
        this.timeRead = timeRead;
        this.image = image;
    }


}
