package com.example.moviebuddy.ui.editmovie;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.databinding.FragmentEditMovieBinding;


public class EditMovie extends Fragment {
    private MovieDatabase myDB;
    private TextView response;
    private TextView movieid;
    private EditText title, directors, cast, date, poster;

    private @NonNull FragmentEditMovieBinding binding;

    public static com.example.moviebuddy.ui.editmovie.EditMovie newInstance() {
        return new com.example.moviebuddy.ui.editmovie.EditMovie();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditMovieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //declare database and form variable
        Context context = getActivity();
        myDB = new MovieDatabase(context);

        response = binding.response;
        movieid = binding.idTxt;
        title = binding.titleTxt;
        directors = binding.directorTxt;
        cast = binding.castTxt;
        date = binding.date;
        poster = binding.posterTxt;

        String[] movieDetails = new String[6];
//        Obtain arguments passed from last fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieDetails = arguments.getStringArray("movieDetails");
            if (movieDetails != null) {
//                Assign movie variables to these arguments
                movieid.setText(movieDetails[0]);
                title.setText(movieDetails[1]);
                directors.setText(movieDetails[2]);
                cast.setText(movieDetails[3]);
                date.setText(movieDetails[4]);
                poster.setText(movieDetails[5]);
            }
        }

//        When user presses update button
        Button updateBtn = binding.updateBtn; // Replace with the correct ID
        updateBtn.setOnClickListener(v -> {
            Update(movieid.getText().toString(),
                    title.getText().toString(),
                    directors.getText().toString(),
                    cast.getText().toString(),
                    date.getText().toString(),
                    poster.getText().toString());
        });

//        When user clicks clear button, reset form
        final Button clearButton = binding.clearMovieBtn;
        clearButton.setOnClickListener(v->{
            title.setText("");
            directors.setText("");
            cast.setText("");
            date.setText("");
            poster.setText("");
        });

//        When user clicks cancel button, take user back to previous fragment
        final Button cancelButton = binding.cancelMovieBtn;
        cancelButton.setOnClickListener(v->{
            GoBack();
        });

        return root;

    }

//    Update movie row
    private void Update(String i, String t, String di, String c, String da, String p) {
        myDB.updateMovie(i, t, di, c, da, p);
        GoBack();

    }

//    Go back to previous fragment
    private void GoBack() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.detach(this);
        transaction.commit();
    }
}