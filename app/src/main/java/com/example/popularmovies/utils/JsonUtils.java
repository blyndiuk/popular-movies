package com.example.popularmovies.utils;
/*
 This class allows to load parse JSON code into instances of Movie class
 */
import android.util.Log;

import com.example.popularmovies.Constants;
import com.example.popularmovies.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link JsonUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name JsonUtils (and an object instance of JsonUtils is not needed).
     */
    private JsonUtils() {
    }

    public static ArrayList<Movie> parseMoviesJson(String json){
        try {
            ArrayList<Movie> movies = new ArrayList<>();
            JSONObject root = new JSONObject(json);

            if(Constants.PAGES_TOTAL == 0) {
                Constants.PAGES_TOTAL = root.getInt("total_pages");
                Log.i(LOG_TAG, "Assign PAGES_TOTAL a new value. PAGES_TOTAL = "+ Constants.PAGES_TOTAL);
            }

            JSONArray resultsJson = root.getJSONArray("results");
            for(int i = 0; i < resultsJson.length(); i++) {
                JSONObject movieJson = resultsJson.getJSONObject(i);
                String movieStringJson = movieJson.toString();
                Movie movie = parseMovieJson(movieStringJson);
                movies.add(movie);
            }

            return movies;

        } catch (JSONException e) {
            Log.e("JsonUtils", "Problem parsing the movies array JSON results", e);
        }
        return null;
    }


    public static Movie parseMovieJson(String json){

        try {
            Movie movie;
            JSONObject root = new JSONObject(json);
            int movieId = root.optInt("id");
            String movieTitle = root.optString("original_title");
            String movieDate = root.optString("release_date");
            String movieOverview = root.optString("overview");
            double movieVoteAverage = root.optDouble("vote_average");
            String moviePosterPath = root.optString("poster_path");

            String imageUrl = "https://image.tmdb.org/t/p/w185" + moviePosterPath;

            movie = new Movie(movieId,movieTitle, movieDate, movieVoteAverage, movieOverview, imageUrl);
            return movie;

        } catch (JSONException e) {
            Log.e("JsonUtils", "Problem parsing the movie JSON results", e);
        }
        return null;
    }

    public static ArrayList<String> parseTrailersJson(String json){

        try {
            ArrayList<String> trailerUrlsList = new ArrayList<>();
            JSONObject root = new JSONObject(json);
            JSONArray resultsJson = root.getJSONArray("results");
            for(int i = 0; i < resultsJson.length(); i++) {

                JSONObject trailerJson = resultsJson.getJSONObject(i);
                String youtubeKey = trailerJson.optString("key");
                String youtubeUrl = "https://www.youtube.com/watch?v=" + youtubeKey;
                trailerUrlsList.add(youtubeUrl);
            }
            return trailerUrlsList;

        } catch (JSONException e) {
            Log.e("JsonUtils", "Problem parsing the trailer JSON results", e);
        }
        return null;
    }


    public static ArrayList<ArrayList<String>> parseReviewsJson(String json){

        try {
            ArrayList<ArrayList<String>> reviewsList = new ArrayList<>();
            ArrayList<String> authors = new ArrayList<>();
            ArrayList<String> contents = new ArrayList<>();

            JSONObject root = new JSONObject(json);
            JSONArray resultsJson = root.getJSONArray("results");

            for(int i = 0; i < resultsJson.length(); i++) {

                JSONObject reviewJson = resultsJson.getJSONObject(i);
                String author = reviewJson.optString("author");
                authors.add(author);
                String content = reviewJson.getString("content");
                contents.add(content);

            }
            reviewsList.add(authors);
            reviewsList.add(contents);
            return reviewsList;

        } catch (JSONException e) {
            Log.e("JsonUtils", "Problem parsing the review JSON results", e);
        }
        return null;
    }
}
