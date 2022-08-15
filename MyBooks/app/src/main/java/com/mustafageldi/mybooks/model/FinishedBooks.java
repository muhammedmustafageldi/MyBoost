package com.mustafageldi.mybooks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FinishedBooks {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "numberOfPages")
    public String numberOfPages;

    @ColumnInfo(name = "image")
    public byte[] image;

    public FinishedBooks(String name,String author,String numberOfPages, byte[] image){
        this.name = name;
        this.author = author;
        this.numberOfPages = numberOfPages;
        this.image = image;
    }
}
