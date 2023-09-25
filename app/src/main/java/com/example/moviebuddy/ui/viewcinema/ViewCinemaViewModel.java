package com.example.moviebuddy.ui.viewcinema;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ViewCinemaViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> cinemaList = new MutableLiveData<>();

//    Get custom cinema list
    public LiveData<ArrayList<String>> getCinemaList() {
        return cinemaList;
    }

//    Add new cinema row to list
    public void addCinema(ArrayList<String> newCinema) {
        cinemaList.setValue(newCinema);
    }
}