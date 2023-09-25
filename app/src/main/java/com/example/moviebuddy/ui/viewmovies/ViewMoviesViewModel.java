package com.example.moviebuddy.ui.viewmovies;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ViewMoviesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> movieList = new MutableLiveData<>();

//  Retrieve the Movie list
    public LiveData<ArrayList<String>> getMovieList() {
        return movieList;
    }

//    Add a new movie to list
    public void addMovie(ArrayList<String> newMovie) {
        movieList.setValue(newMovie);
    }
}