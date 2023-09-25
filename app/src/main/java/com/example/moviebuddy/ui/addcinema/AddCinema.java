package com.example.moviebuddy.ui.addcinema;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moviebuddy.CustomAdapter;
import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.databinding.FragmentAddCinemaBinding;

import java.util.ArrayList;


public class AddCinema extends Fragment {
    private MovieDatabase movieDB;
    private String movieid;
    private boolean hasError = false;
    private boolean recInserted;
    CustomAdapter adapter;
    private @NonNull FragmentAddCinemaBinding binding;

    public static com.example.moviebuddy.ui.addcinema.AddCinema newInstance() {
        return new com.example.moviebuddy.ui.addcinema.AddCinema();
    }

//    AddCinema Fragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddCinemaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = getActivity();
        movieDB = new MovieDatabase(context);

//      Bind UI elements
        final TextView response = binding.response;
        final EditText cinemaid = binding.idTxt;
        final EditText name = binding.nameTxt;
        final EditText location = binding.location;

        ArrayList<String> tableContent = movieDB.retrieveMovieDate();
        final ListView movieRecord = binding.moviedaterec;

        final boolean[] checkBoxes = new boolean[tableContent.size()];

//      Use custom adapter to display movie rows
        adapter = new CustomAdapter(context, tableContent);
        movieRecord.setAdapter(adapter);

        // when add button on the form is clicked
        final Button addButton = binding.addCinemaBtn;
        addButton.setOnClickListener(v -> {
            // Check if input fields are not empty
            // Then add row to cinema database
                if (fieldEmpty(cinemaid)) {
                    response.setText("Please enter a cinema ID.");
                }
                else if (fieldEmpty(name)) {
                    response.setText("Please enter a name.");
                }
                else if (fieldEmpty(location)) {
                    response.setText("Please enter a location.");
                }
                else {
//                    Add row to database
                    recInserted = movieDB.addCinemaRow(cinemaid.getText().toString(), name.getText().toString(), location.getText().toString());

                    if (recInserted) {
                        response.setText(name.getText().toString() + " has been added.");
//                  Clear form
                        name.setText("");
                        location.setText("");
                        hasError = false;
                    }
                    else {
//                      If unsuccessful, throw error
                        response.setText("An error occurred when inserting to database.");
                        hasError = true;
                    }
                }

//            Deal with selecting which movies are showing
//            and putting them into the MovieCinemaDatabase
                if (tableContent.size() > 0) {
//                   Get movie row
                    movieid = tableContent.get(0);
                    boolean[] checkboxes = adapter.getCheckBoxState();
                    for (int i = 0; i < checkboxes.length; i++) {
                        if (checkboxes[i]) {
                            movieid = tableContent.get(i);
//                          Get movieid from movie row
                            String[] parts = movieid.split(",");
                            if (!hasError) {
//                                If no error has occured when adding to cinema table, then proceed
//                                with adding to the movieShowing table
                                movieDB.addShowing(parts[0], cinemaid.getText().toString());
                            }
                        }
                    }
                }
            });

//       When user clicks cancel, reset form
        final Button cancelButton = binding.clearCinemaBtn;
        cancelButton.setOnClickListener(v->{
            cinemaid.setText("");
            name.setText("");
            location.setText("");
        });

        return root;
    }

//    Check if a field is empty/only contains whitespace
    public boolean fieldEmpty(EditText field) {
        boolean isEmpty = false;
        if (isEmpty(field.getText().toString().trim())) {
            isEmpty = true;
        }
        return isEmpty;
    }

}