package com.example.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkingUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MoviesAsyncTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

    private static final String LOG_TAG = "AsyncTask status";


    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;


    public MoviesAsyncTask(Context context, RecyclerView recyclerView){

        this.context = context;
        this.recyclerView = recyclerView;

    }

    @Override
    protected ArrayList<Movie> doInBackground(URL... urls) {
        Log.i(LOG_TAG, "Inside doInBackground");
        Constants.loading = true;

        URL url = NetworkingUtils.createUrl(Constants.URL_TO_BE_USED + Constants.PAGE);

        String jsonResponse = "";
        try {
            jsonResponse = NetworkingUtils.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException thrown");
        }

        ArrayList<Movie> newlyParsedMovies = JsonUtils.parseMoviesJson(jsonResponse);
        if (newlyParsedMovies != null) {

            MainActivity.mMovies.addAll(newlyParsedMovies);
            Log.i(LOG_TAG, "Page #" + Constants.PAGE + ", ADDED 20 new movies. Movie size: " + MainActivity.mMovies.size());
        }
        return MainActivity.mMovies;
    }

    /**
     * Update the screen with the given movies (which was the result of the
     * {@link MoviesAsyncTask}).
     */

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if (movies == null) {
            return;
        }

        if (Constants.PAGE < Constants.PAGES_TOTAL) { //see if there are any pages left
            updateUi(movies);
            Constants.PAGE++;
            Constants.loading = false;
        }
    }

    private void updateUi(ArrayList<Movie> movies) {
        if (Constants.PAGE == 1) {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2); // set a GridLayoutManager with default vertical orientation and 2 number of columns
            recyclerView.setLayoutManager(gridLayoutManager);
            GridAdapter gridAdapter = new GridAdapter(context, movies);
            recyclerView.setAdapter(gridAdapter);
        }
    }
}
