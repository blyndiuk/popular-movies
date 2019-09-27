package com.example.popularmovies.utils;
/*
 This class allows to endlessly load new movies into the UI
 */
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.popularmovies.Constants;
import com.example.popularmovies.GridAdapter;
import com.example.popularmovies.MainActivity;

public class EndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String LOG_TAG = "OnScrollListener status";
    private RecyclerView recyclerView;
    private Context context;
    private GridAdapter gridAdapter;

    public EndlessOnScrollListener(RecyclerView recyclerView, GridAdapter gridAdapter, Context context) {
        this.recyclerView = recyclerView;
        this.gridAdapter = gridAdapter;
        this.context = context;
    }

    public void initScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount;
                if (gridLayoutManager != null) {
                    totalItemCount = gridLayoutManager.getItemCount();
                    int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();

                    boolean endHasBeenReached = !recyclerView.canScrollVertically(1);
                   // boolean end = lastVisibleItemPosition + 5 >= totalItemCount;
                    Log.i(LOG_TAG, "Last visible position:" + lastVisibleItemPosition + ", movies size = " + MainActivity.mMovies.size()
                            + ", RV items: " + totalItemCount + Constants.loading);

                    if (!Constants.loading && totalItemCount > 0 && endHasBeenReached)
                        if(NetworkingUtils.connectedToTheInternet(context))
                            loadMore();
                        else
                            Toast.makeText(context, "Problems with Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMore() {
        Log.i("Log", MainActivity.sortingCriteria);
        Log.i(LOG_TAG, "START  loadMore");
        new MainActivity.MoviesAsyncTask(recyclerView,gridAdapter, context ).execute();
        GridAdapter gridAdapter = (GridAdapter) recyclerView.getAdapter();
        if (gridAdapter != null)
            gridAdapter.notifyDataSetChanged();
    }
}