package com.example.moviebuddy.ui.addmovie;

import static android.text.TextUtils.isEmpty;

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

import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.databinding.FragmentAddMovieBinding;


public class AddMovie extends Fragment {

    private MovieDatabase myDB;
    private boolean recInserted;
    private @NonNull FragmentAddMovieBinding binding;

    public static com.example.moviebuddy.ui.addmovie.AddMovie newInstance() {
        return new com.example.moviebuddy.ui.addmovie.AddMovie();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        AddMovieViewModel addViewModel =
//                new ViewModelProvider(this).get(AddMovieViewModel.class);
        binding = FragmentAddMovieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get elements from fragment UI
        Context context = getActivity();
        myDB = new MovieDatabase(context);
        final TextView response = binding.response;
        final EditText movieid = binding.idTxt;
        final EditText title = binding.titleTxt;
        final EditText directors = binding.directorTxt;
        final EditText cast = binding.castTxt;
        final EditText date = binding.date;
        final EditText poster = binding.posterTxt;

        // When user presses add button
        final Button addButton = binding.addMovieBtn;
        addButton.setOnClickListener(v -> {
            // Insert movie row if required fields are not empty
            if (fieldEmpty(movieid)) {
                response.setText("Please enter a movie ID.");
            }
            else if (fieldEmpty(title)) {
                response.setText("Please enter a movie title.");
            }
            else if (fieldEmpty(directors)) {
                response.setText("Please enter a director.");
            }
            else if (fieldEmpty(cast)) {
                response.setText("Please enter a cast member");
            }
            else if (fieldEmpty(date)) {
                response.setText("Please enter a release date.");
            }
            else {
//                Insert movie row to movie table
                recInserted = myDB.addRow(movieid.getText().toString(), title.getText().toString(), directors.getText().toString(), cast.getText().toString(), date.getText().toString(), poster.getText().toString());

                // Reset the form
                if (recInserted) {
//                    Update response
                    response.setText(title.getText().toString() + " has been added.");
                    movieid.setText("");
                    title.setText("");
                    directors.setText("");
                    cast.setText("");
                    date.setText("");
                    poster.setText("");
                }
                else {
//                  Check for database error
                    response.setText("An error occurred when inserting to database.");
                }
            }
        });

        // When cancel button is clicked, clear the form.
        final Button cancelButton = binding.cancelMovieBtn;
        cancelButton.setOnClickListener(v->{
            movieid.setText("");
            title.setText("");
            directors.setText("");
            cast.setText("");
            date.setText("");
            poster.setText("");
        });

        return root;

    }

    // if the field is empty or only contains whitespace
    public boolean fieldEmpty(EditText field) {
        boolean isEmpty = false;
        if (isEmpty(field.getText().toString().trim())) {
            isEmpty = true;
        }
        return isEmpty;
    }
}