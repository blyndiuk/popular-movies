package com.example.popularmovies;

public class Constants {

    private static final String API_KEY = "";
    //URL to query the movies by rate
    static final String TOP_RATED_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&page=";
    // URL to query the movies by popularity
    static final String MOST_POPULAR_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=";
    static String URL_TO_BE_USED = Constants.MOST_POPULAR_MOVIES_REQUEST_URL;  //URL being used

    public static int PAGES_TOTAL = 0;
    static int PAGE; //page to draw JSON results from

    public static boolean loading;

}
