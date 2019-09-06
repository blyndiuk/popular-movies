package com.example.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.EndlessOnScrollListener;
import com.example.popularmovies.utils.NetworkingUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private  RecyclerView mRecyclerView;
    public static ArrayList<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.PAGE = 1;
        mMovies = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_main_grid);
        Context context = getApplicationContext();


        if (NetworkingUtils.connectedToTheInternet(context)) {  // Check if there is available Internet connection
            
            new MoviesAsyncTask(context, mRecyclerView).execute();
            EndlessOnScrollListener endlessOnScrollListener = new EndlessOnScrollListener(mRecyclerView, context);
            endlessOnScrollListener.initScrollListener();
            setTitle();
        } else
            setNoNetworkUi();
    }


    private void setNoNetworkUi() {       //this method sets UI if there is no Internet connection
        mRecyclerView.setVisibility(View.GONE);
        ImageView noNetworkIv = findViewById(R.id.iv_no_network);
        noNetworkIv.setVisibility(View.VISIBLE);
    }

    private void setTitle(){
        if (Constants.URL_TO_BE_USED.equals(Constants.MOST_POPULAR_MOVIES_REQUEST_URL))
            setTitle(getString(R.string.by_popularity));
        else
            setTitle(getString(R.string.by_rating));
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
            Constants.URL_TO_BE_USED = Constants.MOST_POPULAR_MOVIES_REQUEST_URL;
            recreate();
            return true;

        } else if (id == R.id.action_rating) {
            Constants.URL_TO_BE_USED = Constants.TOP_RATED_MOVIES_REQUEST_URL;
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}