package com.example.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.popularmovies.database.FavoriteMovieEntry;
import com.example.popularmovies.database.FavoriteMoviesDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FavoriteMovieEntry>> tasks;

    public MainViewModel(Application application) {
        super(application);
        FavoriteMoviesDatabase database = FavoriteMoviesDatabase.getInstance(this.getApplication());
        Log.i(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.moviesDao().loadAllTasks();
    }

    public LiveData<List<FavoriteMovieEntry>> getTasks() {
        return tasks;
    }
}