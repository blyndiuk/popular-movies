package com.example.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoriteMoviesDao {

    @Query("SELECT * FROM movie")
    LiveData<List<FavoriteMovieEntry>> loadAllTasks();

    @Query("SELECT * FROM movie WHERE id LIKE :id")
    List<FavoriteMovieEntry> findMovieById(int id);


    @Insert
    void insertTask(FavoriteMovieEntry movieEntry);

    @Delete
    void deleteTask(FavoriteMovieEntry movieEntry);


}
