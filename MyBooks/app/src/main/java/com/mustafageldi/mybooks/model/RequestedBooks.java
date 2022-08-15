package com.mustafageldi.mybooks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RequestedBooks {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "numberOfPages")
    public String numberOfPages;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "summary")
    public String summary;

    @ColumnInfo(name = "image")
    public byte[] image;

    public RequestedBooks(String name,String author,String numberOfPages, String price, String summary, byte[] image){
        this.name = name;
        this.author = author;
        this.numberOfPages = numberOfPages;
        this.price = price;
        this.image = image;
        this.summary = summary;
    }


}
