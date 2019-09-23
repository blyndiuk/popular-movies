package com.example.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    private ArrayList<ArrayList<String>> reviews;


    ReviewsAdapter(ArrayList<ArrayList<String>> reviews) {

        this.reviews = reviews;
    }

     static class MyViewHolder extends RecyclerView.ViewHolder {
         TextView reviewerNameTv;
         TextView reviewTv;

         MyViewHolder(View itemView) {
            super(itemView);
            reviewerNameTv = itemView.findViewById(R.id.tv_reviewer_name);
            reviewTv = itemView.findViewById(R.id.tv_review);
        }
    }

    @NonNull
    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {

        myViewHolder.reviewerNameTv.setText(reviews.get(0).get(position));
        myViewHolder.reviewTv.setText(reviews.get(1).get(position));

    }

    @Override
    public int getItemCount() {
        return reviews.get(1).size();
    }
}