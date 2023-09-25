package com.example.moviebuddy.ui.editcinema;

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
import androidx.fragment.app.FragmentTransaction;
import com.example.moviebuddy.CustomAdapter;
import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.databinding.FragmentEditCinemaBinding;

import java.util.ArrayList;

public class EditCinema extends Fragment {

    private MovieDatabase myDB;
    CustomAdapter adapter;
    private TextView response;
    private TextView cinemaid;
    private EditText name;
    private EditText location;
    private ListView movieRecord;
    private boolean[] checkBoxes;
    private @NonNull FragmentEditCinemaBinding binding;

    public static com.example.moviebuddy.ui.editcinema.EditCinema newInstance() {
        return new com.example.moviebuddy.ui.editcinema.EditCinema();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditCinemaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = getActivity();
        myDB = new MovieDatabase(context);

        response = binding.response;
        cinemaid = binding.idTxt;
        name = binding.nameTxt;
        location = binding.location;

//       Get movie-date pairs from movie database
        ArrayList<String> tableContent = myDB.retrieveMovieDate();
//      Movie list
        movieRecord = binding.moviedaterec;
//      Define checkboxes array as same length as movies array
        checkBoxes = new boolean[tableContent.size()];
        // Use custom adapter
        adapter = new CustomAdapter(context, tableContent);
//        Assign adapter to movie list
        movieRecord.setAdapter(adapter);

//        A single cinema row
        String[] cinemaDetails = new String[3];
//        Retrieve arguments passed from last fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            cinemaDetails = arguments.getStringArray("cinemaDetails");
            if (cinemaDetails != null) {
//              Assign cinema variables to these arguments
                cinemaid.setText(cinemaDetails[0]);
                name.setText(cinemaDetails[1]);
                location.setText(cinemaDetails[2]);
            }
        }

//        When user presses update button
        Button updateBtn = binding.updateCinemaBtn;
        updateBtn.setOnClickListener(v -> {
            Update(cinemaid,
                    name,
                    location);
        });


        // When clear button is clicked, reset the form.
        final Button clearButton = binding.clearCinemaBtn;
        clearButton.setOnClickListener(v->{
            name.setText("");
            location.setText("");
        });

//        When cancel button is clicked, take user to previous fragment
        final Button cancelButton = binding.cancelCinemaBtn;
        cancelButton.setOnClickListener(v->{
            GoBack();
        });

        return root;

    }

    private void Update(TextView cinemaid, EditText name, EditText location) {
        CustomAdapter adapter;
        Context context = getActivity();
        boolean hasError = false;
        String movieRow;
//        Get movies to display as a list to select
        ArrayList<String> tableContent = myDB.retrieveMovieDate();
        adapter = new CustomAdapter(context, tableContent);

        ArrayList<String> movies = new ArrayList<>();
        ListView movieRecord = binding.moviedaterec;
        movieRecord.setAdapter(adapter);
        boolean[] checkboxes = adapter.getCheckBoxState();

//        Check required fields are filled
            if (fieldEmpty(name)) {
                response.setText("Please enter a name.");
            }
            else if (fieldEmpty(location)) {
                response.setText("Please enter a location.");
            }
            else {
                for (int i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i]) {
                        movieRow = tableContent.get(i);
    //                   Get movieid from movie row
                        String[] parts = movieRow.split(",");
//                       Add movieID to arrayList
                        movies.add(parts[0]);
                    }
//                  Update cinema row
                    myDB.updateCinema(cinemaid.getText().toString(), name.getText().toString(), location.getText().toString(), movies);
                }
            }
//      Go back to previous fragment
        GoBack();

    }

//    Go back to previous fragment
    private void GoBack() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.detach(this);
        transaction.commit();
    }

    public boolean fieldEmpty(EditText field) {
//        if the field is empty or only contains whitespace
        boolean isEmpty = false;
        if (field.getText().toString().trim() == "") {
            isEmpty = true;
        }
        return isEmpty;
    }
}