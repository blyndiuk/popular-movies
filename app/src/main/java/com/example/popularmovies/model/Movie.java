package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public final static String MOVIE_INTENT = "movie";


    private String title;
    private String date;
    private double voteAvg;
    private String overview;
    private String imageUrl;

    /**
     * No args constructor for use in serialization
     */
    public Movie(){

    }

    public Movie(String title, String date, double voteAvg, String overview, String imageUrl) {
        this.title = title;
        this.date = date;
        this.voteAvg = voteAvg;
        this.overview = overview;
        this.imageUrl = imageUrl;
    }

    private Movie (Parcel in){
        title = in.readString();
        date = in.readString();
        voteAvg = in.readDouble();
        overview = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(date);
        dest.writeDouble(voteAvg);
        dest.writeString(overview);
        dest.writeString(imageUrl);

    }

    //used to decode what's in the parcel
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) { return new Movie(parcel); }
        @Override
        public Movie[] newArray(int i) { return new Movie[i];}
    };


    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getVoteAvg() { return voteAvg; }

    public String getOverview() {
        return overview;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVoteAvg(double voteAvg) { this.voteAvg = voteAvg; }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
