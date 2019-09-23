package com.example.popularmovies;

public class Constants {

    static final String API_KEY = "";

    static final String FAVORITE_MOVIES = "favorite_movies";
    static final String POPULAR_MOVIES = "popular_movies";
    static final String TOP_RATED_MOVIES = "top_rated_movies";
    static final String DOMAIN = "https://api.themoviedb.org/3/movie/";
    //URL to query the movies by rate
    static final String TOP_RATED_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&page=";
    // URL to query the movies by popularity
    static final String MOST_POPULAR_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=";
    static String URL_TO_BE_USED = MOST_POPULAR_MOVIES_REQUEST_URL;  //URL being used
    static final String TRAILER_REQUEST_QUERY = "/videos?api_key="+API_KEY+"&language=en-US";
    static final String REVIEW_REQUEST_QUERY = "/reviews?api_key="+API_KEY+"&language=en-US";


    public static int PAGES_TOTAL = 0;
    static int PAGE; //page to draw JSON results from

    public static boolean loading;

}
