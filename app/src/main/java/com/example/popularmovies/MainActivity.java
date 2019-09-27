package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Parcelable;
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

public class MainActivity extends AppCompatActivity {
   // public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    public  RecyclerView mRecyclerView;
    public static ArrayList<Movie> mMovies;
    public  GridAdapter mGridAdapter;
    //Use SharedPreferences to save a list of favorite movies and update the ui (the star icon) depending on whether the movie is favorite or not
    public static SharedPreferences preferences;
    public static String sortingCriteria = Constants.POPULAR_MOVIES;//when the activity is created for the first time, the movies are sorted by popularity
    public static final String MOVIES_STATE = "movies_state";
    public static final String BUNDLE_RECYCLERVIEW_LAYOUT = "recyclerview_layout";
    public static final String PAGE_STATE = "page_state";
    private Parcelable savedRecyclerViewLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Log", "inside onCreate");
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView = findViewById(R.id.rv_main_grid);

        if (savedInstanceState != null){
            if (NetworkingUtils.connectedToTheInternet(this) || sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
                savedRecyclerViewLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLERVIEW_LAYOUT);
                Constants.PAGE = savedInstanceState.getInt(PAGE_STATE);
                displayData();
                if (!sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {
                    EndlessOnScrollListener endlessOnScrollListener = new EndlessOnScrollListener(mRecyclerView, mGridAdapter, this);
                    endlessOnScrollListener.initScrollListener();
                } else {
                    setupViewModel();
                }
            } else
                initViews();
        } else {
                initViews();
        }
        setTitle();
    }

    private void restoreLayoutManagerPosition(){
        if(mRecyclerView.getLayoutManager()!=null) {
            Log.i("Status", "Restoring layout manager position");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // save the movie list data
        if(mMovies != null) {
            savedInstanceState.putParcelableArrayList(MOVIES_STATE, mMovies);
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager != null)
                savedInstanceState.putParcelable(BUNDLE_RECYCLERVIEW_LAYOUT, layoutManager.onSaveInstanceState());
            savedInstanceState.putInt(PAGE_STATE, Constants.PAGE);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
            savedRecyclerViewLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLERVIEW_LAYOUT);
            Constants.PAGE = savedInstanceState.getInt(PAGE_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //setting up ViewModel that allows to keep the results from ROOM db in memory
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<FavoriteMovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovieEntry> favoriteMovieEntries) {
                Log.i("Status", "Updating list of tasks from LiveData in ViewModel");
                if(sortingCriteria.equals(Constants.FAVORITE_MOVIES))
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
            Log.i("Log", "inside doInBackground");
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
            }
            return MainActivity.mMovies;
        }


        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            Log.i("Log", "inside onPostExecute");
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
        GridLayoutManager gridLayoutManager;
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(context, 2); // set a GridLayoutManager with default vertical orientation and 2 number of columns
        } else {
            gridLayoutManager = new GridLayoutManager(context, 3); // set a GridLayoutManager with default horizontal orientation and 3 number of columns
        }
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(gridAdapter);
    }

    private void displayData(){
        if (NetworkingUtils.connectedToTheInternet(this)) {
            setWorkingNetworkUi();
            mGridAdapter = new GridAdapter(this, mMovies);
            updateUi(mRecyclerView,mGridAdapter, this);
            restoreLayoutManagerPosition();
        } else {
            if(!sortingCriteria.equals(Constants.FAVORITE_MOVIES)) {
                setNoNetworkUi();
            } else {
                setWorkingNetworkUi(); //even though there is no internet connection, the ui stays the same
                mGridAdapter = new GridAdapter(this, mMovies);
                updateUi(mRecyclerView, mGridAdapter, this);
                restoreLayoutManagerPosition();
            }
        }
    }

    private void initViews(){
        if (NetworkingUtils.connectedToTheInternet(this)) {
            setWorkingNetworkUi();
            Constants.PAGE = 1;
            mMovies = new ArrayList<>();
            mGridAdapter = new GridAdapter(this, mMovies);
            updateUi(mRecyclerView, mGridAdapter, this);
            EndlessOnScrollListener endlessOnScrollListener = new EndlessOnScrollListener(mRecyclerView, mGridAdapter, this);
            endlessOnScrollListener.initScrollListener();
            new MoviesAsyncTask(mRecyclerView, mGridAdapter, this).execute();
        } else {
            if(mMovies == null)
                setNoNetworkUi();
            else {
                setWorkingNetworkUi();
                mGridAdapter = new GridAdapter(this, mMovies);
                updateUi(mRecyclerView, mGridAdapter, this);
                restoreLayoutManagerPosition();
                EndlessOnScrollListener endlessOnScrollListener = new EndlessOnScrollListener(mRecyclerView, mGridAdapter, this);
                endlessOnScrollListener.initScrollListener();
            }
        }
    }

    private void initViewsFavorite(){
        mRecyclerView.clearOnScrollListeners();
        setWorkingNetworkUi();
        Constants.PAGE = 1;
        mMovies = new ArrayList<>();
        mGridAdapter = new GridAdapter(this, mMovies);
        updateUi(mRecyclerView, mGridAdapter, this);
        setupViewModel();
    }

    //this methods sets UI depending on Internet connection
    private void setNoNetworkUi() {
        mMovies = null;
        mRecyclerView.setVisibility(View.GONE);
        ImageView noNetworkIv = findViewById(R.id.iv_no_network);
        noNetworkIv.setVisibility(View.VISIBLE);
    }
    private void setWorkingNetworkUi(){
        ImageView noNetworkIv = findViewById(R.id.iv_no_network);
        noNetworkIv.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

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
                mMovies = null;
                Constants.URL_TO_BE_USED = Constants.MOST_POPULAR_MOVIES_REQUEST_URL;
                initViews();
                setTitle();
                break;
            case(R.id.action_rating):
                sortingCriteria = Constants.TOP_RATED_MOVIES;
                mMovies = null;
                Constants.URL_TO_BE_USED = Constants.TOP_RATED_MOVIES_REQUEST_URL;
                initViews();
                setTitle();
                break;
            case(R.id.action_favorite):
                sortingCriteria = Constants.FAVORITE_MOVIES;
                mMovies = null;
                initViewsFavorite();
                setTitle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}