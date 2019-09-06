package com.example.popularmovies;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class GridAdapter extends RecyclerView.Adapter <GridAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList <Movie> movies;


   GridAdapter(Context context, ArrayList <Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the item Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        // set the data in items
        final Movie movie = movies.get(position);
        //use picasso library to populate the view with images
        Picasso.with(context)
                .load(movie.getImageUrl())
                .into(viewHolder.image);

        // implement setOnClickListener event on item view.
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Movie.MOVIE_INTENT, movie);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView image;
        MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
