package com.example.moviebuddy.ui.viewmovies;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviebuddy.CustomAdapter;
import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.R;
import com.example.moviebuddy.databinding.FragmentViewMoviesBinding;
import com.example.moviebuddy.ui.editmovie.EditMovie;

import java.util.ArrayList;

public class ViewMovies extends Fragment {
    private MovieDatabase mydManger;
    private TextView displayMessage;
    private ListView movieRecord;
    private TextView movieRec;
    private Button editButton;
    private Button deleteButton;
    private boolean[] checkBoxes;
    CustomAdapter adapter;
    private FragmentViewMoviesBinding binding;
    private Object view;

    public static com.example.moviebuddy.ui.viewmovies.ViewMovies newInstance() {
        return new com.example.moviebuddy.ui.viewmovies.ViewMovies();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewMoviesViewModel viewMoviesViewModel =
                new ViewModelProvider(this).get(ViewMoviesViewModel.class);
        binding = FragmentViewMoviesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = getActivity();
        mydManger = new MovieDatabase(context);
        mydManger.openReadable();

        viewMoviesViewModel.getMovieList().observe(this, newMovie -> {
            // Update the UI with the new data
            viewMoviesViewModel.addMovie(newMovie);
        });

        // retrieve data from the database and display as custom ListView
        ArrayList<String> tableContent = mydManger.retrieveRows();
        displayMessage = binding.disResponse;
        movieRecord = binding.movierec;
        displayMessage.setText("Movies\n");

        checkBoxes = new boolean[tableContent.size()];
        // use custom layout
        adapter = new CustomAdapter(context, tableContent);
        movieRecord.setAdapter(adapter);

        Button editBtn = binding.editBtn;
//      When user clicks edit
        editBtn.setOnClickListener(v -> {
            Edit(tableContent);
        });

//  When user clicks delete
        Button deleteBtn = binding.deleteBtn;
        deleteBtn.setOnClickListener(v -> {
            Delete(tableContent);
            if (movieRecord != null) {
//                update adapter
                movieRecord.invalidateViews();
            }
            adapter.notifyDataSetChanged();
        });

        return root;

    }

    private void Delete(ArrayList<String> movieTable) {
        String movieRow;
        boolean[] checkboxes = adapter.getCheckBoxState();
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i]) {
                movieRow = movieTable.get(i);
                String[] parts = movieRow.split(",");
                // Delete selected movies
                mydManger.deleteMovies(parts[0]);
            }
        }
    }

    public void Edit(ArrayList<String> movieTable) {
        String[] movieResults;
        Boolean isSelected = false;
        String movieRow;
        String movieDetails[];
        boolean[] checkboxes = adapter.getCheckBoxState();
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i]) {
                isSelected = true;
                movieRow = movieTable.get(i);
                movieDetails = movieRow.split(",");
                // Update selected items
                movieResults = mydManger.getMovieDetails(movieDetails);

                Bundle bundle = new Bundle();
                bundle.putStringArray("movieDetails", movieResults);

                EditMovie editmoviefragment = new EditMovie();
                editmoviefragment.setArguments(bundle);
                // Navigate to the edit movie fragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, editmoviefragment);
//                User can go back to previous fragment
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        Context applicationContext = getActivity().getApplicationContext();

        if (checkboxes.length > 0) {
            // If nothing has been selected
            if (!isSelected) {
                Toast.makeText(applicationContext, "Please select a movie to edit.", Toast.LENGTH_SHORT).show();
            }
        }
        //      If there are no movies
        else {
            Toast.makeText(applicationContext, "There are no movies to edit.", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

}


