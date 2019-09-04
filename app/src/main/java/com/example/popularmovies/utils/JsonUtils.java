package com.example.popularmovies.utils;

import android.util.Log;

import com.example.popularmovies.MainActivity;
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

            if(MainActivity.PAGES_TOTAL == 0) {
                MainActivity.PAGES_TOTAL = root.getInt("total_pages");
                Log.i(LOG_TAG, "Assign PAGES_TOTAL a new value. PAGES_TOTAL = "+ MainActivity.PAGES_TOTAL);
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
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the movies array JSON results", e);
        }
        return null;
    }


    public static Movie parseMovieJson(String json){

        try {
            Movie movie;
            JSONObject root = new JSONObject(json);
            String movieTitle = root.optString("original_title");
            String movieDate = root.optString("release_date");
            String movieOverview = root.optString("overview");
            double movieVoteAverage = root.optDouble("vote_average");
            String moviePosterPath = root.optString("poster_path");

            String imageUrl = "https://image.tmdb.org/t/p/w185" + moviePosterPath;

            movie = new Movie(movieTitle, movieDate, movieVoteAverage, movieOverview, imageUrl);
            return movie;

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the movie JSON results", e);
        }
        return null;
    }

}
