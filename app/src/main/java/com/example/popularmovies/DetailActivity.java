package com.example.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get movie info from the previous activity
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }

        Movie movie = intent.getParcelableExtra(Movie.MOVIE_INTENT);
        if(movie == null){
            closeOnError();
            return;
        }

        populateUI(movie);
    }

    private void populateUI(Movie movie){
        ImageView posterIv = findViewById(R.id.iv_movie_poster);
        TextView titleTv =  findViewById(R.id.tv_movie_title);
        TextView overviewTv = findViewById(R.id.tv_movie_overview);
        TextView voteAvgTv = findViewById(R.id.tv_vote_average);
        TextView dateTv = findViewById(R.id.tv_release_date);


        Picasso.with(this)
                .load(movie.getImageUrl())
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(posterIv);


       titleTv.setText(movie.getTitle());
       overviewTv.setText(movie.getOverview());
       voteAvgTv.setText(String.valueOf(movie.getVoteAvg()));
       String formattedDate = "(" + movie.getDate().substring(0,4) + ")";
       dateTv.setText(formattedDate);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
