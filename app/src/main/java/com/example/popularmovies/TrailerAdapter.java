package com.example.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {
    private ArrayList<String> mUrls;
    private Context context;

    TrailerAdapter(ArrayList<String> myUrls, Context context) {
        this.context = context;
        mUrls = myUrls;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_trailer);
        }
    }

    @NonNull
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int position) {

        myViewHolder.textView.setText(R.string.trailer);
        myViewHolder.textView.append(" "+ (position+1));
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mUrls.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }
}