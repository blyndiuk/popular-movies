package com.example.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkingUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "";
    private static final String LOG_TAG = "status";

    //URL to query the movies by rate
    private static final String TOP_RATED_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + MainActivity.API_KEY + "&language=en-US&page=";
    // URL to query the movies by popularity
    private static final String MOST_POPULAR_MOVIES_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + MainActivity.API_KEY + "&language=en-US&page=";
    //URL being used
    private static String URL_TO_BE_USED = MOST_POPULAR_MOVIES_REQUEST_URL;

    private RecyclerView mRecyclerView;
    private GridAdapter mGridAdapter;
    private ArrayList<Movie> mMovies;

    //page to draw JSON results from
    private static int PAGE;
    public static int PAGES_TOTAL = 0;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PAGE = 1;
        mMovies = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_main_grid);



        // Check if there is available Internet connection
        if (connectedToTheInternet()) {

            new MoviesAsyncTask().execute();
            initScrollListener();
            if (URL_TO_BE_USED.equals(MOST_POPULAR_MOVIES_REQUEST_URL))
                setTitle(getString(R.string.by_popularity));
            else
                setTitle(getString(R.string.by_rating));
        } else
            setNoNetworkUi();
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the movies arrayList.
     */
    private class MoviesAsyncTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            loading = true;

            URL url = NetworkingUtils.createUrl(URL_TO_BE_USED + PAGE);

            String jsonResponse = "";
            try {
                jsonResponse = NetworkingUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown");
            }

            ArrayList<Movie> newlyParsedMovies = JsonUtils.parseMoviesJson(jsonResponse);
            if (newlyParsedMovies != null) {

                mMovies.addAll(newlyParsedMovies);
                Log.i(LOG_TAG, "Page #" + PAGE + ", ADDED 20 new movies. Movie size: " + mMovies.size());
            }
            return mMovies;
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
            //see if there are any pages left
            if (PAGE < PAGES_TOTAL) {
                updateUi(movies);
                PAGE++;
                loading = false;
            }
        }
    }

    private void updateUi(ArrayList<Movie> movies) {
        if (PAGE == 1) {
            // set a GridLayoutManager with default vertical orientation and 2 number of columns
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mGridAdapter = new GridAdapter(this, movies);
            mRecyclerView.setAdapter(mGridAdapter);
        }
    }

    //checks if there is Internet connection
    private boolean connectedToTheInternet() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    //this method sets UI if there is no Internet connection
    private void setNoNetworkUi() {

        mRecyclerView.setVisibility(View.GONE);
        ImageView noNetworkIv = findViewById(R.id.iv_no_network);
        noNetworkIv.setVisibility(View.VISIBLE);
    }

    //handling endless scroll

    private void initScrollListener() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = gridLayoutManager.getItemCount();
                int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached = !recyclerView.canScrollVertically(1);
                Log.i(LOG_TAG, "Last visible position:" + lastVisibleItemPosition + ", movies size = " + mMovies.size()
                        + ", RV items: " + totalItemCount  + loading);

                if(!loading && totalItemCount>0 && endHasBeenReached)
                    loadMore();
            }
        });
    }

    private void loadMore() {
        Log.i(LOG_TAG, "START  loadMore");
        if (connectedToTheInternet()) {
            new MoviesAsyncTask().execute();
            mGridAdapter.notifyDataSetChanged();
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.poor_connection), Toast.LENGTH_SHORT).show();
    }

    // Methods for setting up the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sorting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popularity) {
            URL_TO_BE_USED = MOST_POPULAR_MOVIES_REQUEST_URL;
            recreate();
            return true;

        } else if (id == R.id.action_rating) {
            URL_TO_BE_USED = TOP_RATED_MOVIES_REQUEST_URL;
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
        
    }
}