package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.database.FavoriteMovieEntry;
import com.example.popularmovies.database.FavoriteMoviesDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkingUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AsyncTask status";

    ImageView starIv;
    FavoriteMoviesDatabase mDb;
    public static ArrayList<String> trailerUrls;
    public static ArrayList<ArrayList<String>> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        trailerUrls = new ArrayList<>();
        RecyclerView trailersRecyclerView = findViewById(R.id.rv_trailers);
        RecyclerView reviewRecyclerView =  findViewById(R.id.rv_reviews);
        reviews = new ArrayList<>();


        // get movie info from the previous activity
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }
        final Movie movie = intent.getParcelableExtra(Movie.MOVIE_INTENT);
        if(movie == null){
            closeOnError();
            return;
        }
        populateUI(movie);
        new TrailersAsyncTask(movie.getId(), trailersRecyclerView, this).execute();
        new ReviewsAsyncTask(movie.getId(),reviewRecyclerView, this).execute();


        starIv = findViewById(R.id.iv_star);
        starIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onAddOrRemoveFavoriteMovie(movie);
            }
        });
    }

    private static class TrailersAsyncTask extends AsyncTask<URL, Void, ArrayList<String>> {

        int movieId;
        private WeakReference<RecyclerView> recyclerViewWeakReference;
        private WeakReference<Context> contextWeakReference;

        TrailersAsyncTask(int movieId, RecyclerView recyclerView, Context context){
            this.movieId = movieId;
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<String> doInBackground(URL... urls) {
            URL url = NetworkingUtils.createUrl(Constants.DOMAIN + movieId + Constants.TRAILER_REQUEST_QUERY);
                String jsonResponse = "";
                try {
                    jsonResponse = NetworkingUtils.makeHttpRequest(url);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IOException thrown");
                }
            trailerUrls = JsonUtils.parseTrailersJson(jsonResponse);
            return trailerUrls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> trailerUrls) {
            if (trailerUrls == null) {
                return;
            }
            TrailerAdapter trailerAdapter = new TrailerAdapter(trailerUrls, contextWeakReference.get());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(contextWeakReference.get());
            recyclerViewWeakReference.get().setLayoutManager(linearLayoutManager);
            recyclerViewWeakReference.get().setAdapter(trailerAdapter);
        }
    }

    private static class ReviewsAsyncTask extends AsyncTask<URL, Void, ArrayList<ArrayList<String>>> {

        int movieId;
        private WeakReference<Context> contextWeakReference;
        private WeakReference<RecyclerView> recyclerViewWeakReference;

        ReviewsAsyncTask(int movieId, RecyclerView recyclerView, Context context){
            this.movieId = movieId;
            contextWeakReference = new WeakReference<>(context);
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(URL... urls) {
            URL url = NetworkingUtils.createUrl(Constants.DOMAIN + movieId + Constants.REVIEW_REQUEST_QUERY);
            String jsonResponse = "";
            try {
                jsonResponse = NetworkingUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown");
            }
            return JsonUtils.parseReviewsJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> reviews) {
            if (reviews == null) {
                return;
            }
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(contextWeakReference.get());
            recyclerViewWeakReference.get().setLayoutManager(linearLayoutManager);
            recyclerViewWeakReference.get().setAdapter(reviewsAdapter);
        }
    }

    private void populateUI(Movie movie){
        ImageView posterIv = findViewById(R.id.iv_movie_poster);
        TextView titleTv =  findViewById(R.id.tv_movie_title);
        TextView overviewTv = findViewById(R.id.tv_movie_overview);
        TextView voteAvgTv = findViewById(R.id.tv_vote_average);
        TextView dateTv = findViewById(R.id.tv_release_date);
        ImageView starIv =  findViewById(R.id.iv_star);

        Picasso.with(this)
                .load(movie.getImageUrl())
                .error(R.mipmap.image_placeholder)
                .placeholder(R.mipmap.image_placeholder)
                .into(posterIv);

       titleTv.setText(movie.getTitle());
       overviewTv.setText(movie.getOverview());
       voteAvgTv.setText(String.valueOf(movie.getVoteAvg()));
       voteAvgTv.append(getResources().getString(R.string.out_of_ten));
       String formattedDate = "(" + movie.getDate().substring(0,4) + ")";
       dateTv.setText(formattedDate);

        if(MainActivity.preferences.contains(movie.getId()+"")){
            starIv.setImageResource(R.drawable.ic_star_yellow_50dp);
        }
    }

    public void onAddOrRemoveFavoriteMovie(final Movie movie) {

        final FavoriteMovieEntry task = new FavoriteMovieEntry(movie.getId(), movie.getTitle(),
                movie.getDate(), movie.getVoteAvg(), movie.getOverview(), movie.getImageUrl());
        mDb = FavoriteMoviesDatabase.getInstance(getApplicationContext());

        SharedPreferences.Editor editor = MainActivity.preferences.edit();

        if (!MainActivity.preferences.contains(movie.getId() + "")){
            editor.putString(movie.getId()+"", "");
            Toast.makeText(getApplicationContext(), "Movie added to favorite", Toast.LENGTH_LONG).show();
        } else {
            editor.remove(movie.getId() + "");
            Toast.makeText(getApplicationContext(), "Movie removed from favorite", Toast.LENGTH_LONG).show();
        }
        editor.apply();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mDb.moviesDao().findMovieById(movie.getId()).isEmpty()) {
                    // insert new movie
                    mDb.moviesDao().insertTask(task);
                    Log.i("status: ", " movie inserted into db");
                    starIv.setImageResource(R.drawable.ic_star_yellow_50dp);

                } else {
                    //remove movie
                    mDb.moviesDao().deleteTask(task);
                    Log.i("status: ", " movie deleted from db");
                    starIv.setImageResource(R.drawable.ic_star_border_white_50dp);
                }
            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
