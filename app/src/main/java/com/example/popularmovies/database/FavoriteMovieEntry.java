package com.example.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie")
public class FavoriteMovieEntry {
    @PrimaryKey
    private int id;
    private String title;
    private String date;
    @ColumnInfo(name = "vote_avg")
    private double voteAvg;
    private String overview;
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public FavoriteMovieEntry(int id, String title, String date, double voteAvg, String overview, String imageUrl){
        this.id = id;
        this.title = title;
        this.date = date;
        this.voteAvg = voteAvg;
        this.overview = overview;
        this.imageUrl = imageUrl;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVoteAvg(double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getVoteAvg() {
        return voteAvg;
    }

    public String getOverview() {
        return overview;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
