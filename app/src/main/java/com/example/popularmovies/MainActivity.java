package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import com.example.popularmovies.database.FavoriteMovieEntry;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.EndlessOnScrollListener;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkingUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public  RecyclerView mRecyclerView;
    public static ArrayList<Movie> mMovies;
    public  GridAdapter mGridAdapter;
    //Use SharedPreferences to save a list of favorite movies and update the ui (the star icon) depending on whether the movie is favorite or not
    //  without having to call the database and in order to implement onSharedPreferenceChanged when we add or remove favorite movies
    public static SharedPreferences preferences;
    public static String sortingCriteria = Constants.POPULAR_MOVIES;//when the activity is created for the first time, the movies are sorted by popularity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovies = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_main_grid);
        mGridAdapter = new GridAdapter(this, mMovies);
        Context context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);


        if (NetworkingUtils.connectedToTheInternet(context)) {  // Check if there is available Internet connection
            Constants.PAGE = 1;
            new MoviesAsyncTask(mRecyclerView, mGridAdapter, context).execute();
            if (!sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {
                EndlessOnScrollListener endlessOnScrollListener = new EndlessOnScrollListener(mRecyclerView, mGridAdapter, context);
                endlessOnScrollListener.initScrollListener();
            }
        } else {
            if (!sortingCriteria.equals(Constants.FAVORITE_MOVIES))
                setNoNetworkUi();
        }
        setTitle();

         if(sortingCriteria.equals(Constants.FAVORITE_MOVIES) && !NetworkingUtils.connectedToTheInternet(context)) {

                Log.i("status :", "not connected");
                updateUi(mRecyclerView, mGridAdapter, context);
                setTitle();
                setupViewModel();
         }
    }

    //setting up ViewModel that allows to keep the results from ROOM db in memory when the phone rotates
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<FavoriteMovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovieEntry> favoriteMovieEntries) {
                Log.d("Status", "Updating list of tasks from LiveData in ViewModel");
                mGridAdapter.setMovies(favoriteMovieEntries);
            }
        });
    }

    public static class MoviesAsyncTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

        private static final String LOG_TAG = "AsyncTask status";
        private WeakReference<RecyclerView> recyclerViewWeakReference;
        private WeakReference<Context> contextWeakReference;
        private  GridAdapter gridAdapter;

        public MoviesAsyncTask(RecyclerView recyclerView, GridAdapter gridAdapter, Context context){
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
            contextWeakReference = new WeakReference<>(context);
            this.gridAdapter = gridAdapter;
        }


        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {

            if (MainActivity.sortingCriteria.equals(Constants.POPULAR_MOVIES)||
                    MainActivity.sortingCriteria.equals(Constants.TOP_RATED_MOVIES)) {
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


            } else if(MainActivity.sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {

                preferences = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
                Map<String, ?> allEntries = preferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String id = entry.getKey();
                    URL url = NetworkingUtils.createUrl( "https://api.themoviedb.org/3/movie/"+ id + "?api_key="+ Constants.API_KEY);

                    String jsonResponse = "";
                    try {
                        jsonResponse = NetworkingUtils.makeHttpRequest(url);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "IOException thrown");
                    }

                    Movie newlyParsedMovie = JsonUtils.parseMovieJson(jsonResponse);
                    if (newlyParsedMovie != null) {
                        MainActivity.mMovies.add(newlyParsedMovie);
                    }
                }
            }
            return MainActivity.mMovies;
        }


        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies == null) {
                return;
            }
            if(Constants.PAGE == 1) {
                updateUi(recyclerViewWeakReference.get(), gridAdapter, contextWeakReference.get());
            }

            if (sortingCriteria.equals(Constants.TOP_RATED_MOVIES) || sortingCriteria.equals(Constants.POPULAR_MOVIES)) { //see if there are any pages left
                if (Constants.PAGE < Constants.PAGES_TOTAL) {
                    Constants.PAGE++;
                    Constants.loading = false;
                }
            }
        }
    }

    private static void updateUi(RecyclerView recyclerView, GridAdapter gridAdapter, Context context) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2); // set a GridLayoutManager with default vertical orientation and 2 number of columns
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(gridAdapter);
    }

    //this method sets UI if there is no Internet connection
    private void setNoNetworkUi() {
        mRecyclerView.setVisibility(View.GONE);
        ImageView noNetworkIv = findViewById(R.id.iv_no_network);
        noNetworkIv.setVisibility(View.VISIBLE);
    }

    //setting title depending on sorting criteria
    private void setTitle(){
        switch (sortingCriteria) {
            case Constants.POPULAR_MOVIES:
                setTitle(getString(R.string.by_popularity));
                break;
            case Constants.TOP_RATED_MOVIES:
                setTitle(getString(R.string.by_rating));
                break;
            case Constants.FAVORITE_MOVIES:
                setTitle("My Favorite");
                break;
        }
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
        switch (id){
            case(R.id.action_popularity):
                sortingCriteria = Constants.POPULAR_MOVIES;
                Constants.URL_TO_BE_USED = Constants.MOST_POPULAR_MOVIES_REQUEST_URL;
                recreate();
                break;
            case(R.id.action_rating):
                sortingCriteria = Constants.TOP_RATED_MOVIES;
                Constants.URL_TO_BE_USED = Constants.TOP_RATED_MOVIES_REQUEST_URL;
                recreate();
                break;
            case(R.id.action_favorite):
                sortingCriteria = Constants.FAVORITE_MOVIES;
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

     // Methods for managing onSharedPreferenceChangeListener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {
            Constants.PAGE = 1;
            mMovies = new ArrayList<>();
            mGridAdapter = new GridAdapter(this, mMovies);
            new MoviesAsyncTask(mRecyclerView, mGridAdapter, getApplicationContext()).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}