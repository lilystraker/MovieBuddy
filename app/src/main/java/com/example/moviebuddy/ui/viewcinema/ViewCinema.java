package com.example.moviebuddy.ui.viewcinema;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviebuddy.CustomAdapter;
import com.example.moviebuddy.MovieDatabase;
import com.example.moviebuddy.R;
import com.example.moviebuddy.databinding.FragmentViewCinemaBinding;
import com.example.moviebuddy.ui.editcinema.EditCinema;

import java.util.ArrayList;


public class ViewCinema extends Fragment {
    private MovieDatabase mydManger;
    private TextView displayMessage;
    private ListView cinemaRecord;
    private TextView cinemaRec;
    private Button editButton;
    private Button deleteButton;

    private ViewCinemaViewModel viewCinemaViewModel;
    CustomAdapter adapter;
    private FragmentViewCinemaBinding binding;
    private Object view;
    private boolean[] checkBoxes;

    public static com.example.moviebuddy.ui.viewcinema.ViewCinema newInstance() {
        return new com.example.moviebuddy.ui.viewcinema.ViewCinema();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewCinemaViewModel viewCinemaViewModel =
                new ViewModelProvider(this).get(ViewCinemaViewModel.class);
        binding = FragmentViewCinemaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = getActivity();
        mydManger = new MovieDatabase(context);
        mydManger.openReadable();

        viewCinemaViewModel.getCinemaList().observe(this, newCinema -> {
            // Update the UI with the new data
            viewCinemaViewModel.addCinema(newCinema);
        });

        // retrieve data from the database and display as ListView
        ArrayList<String> tableContent = mydManger.retrieveCinema();
        displayMessage = binding.disResponse;
//         list
        ListView cinemaRecord = binding.cinemarec;
        displayMessage.setText("Cinemas\n");

        checkBoxes = new boolean[tableContent.size()];
        // use custom layout
        adapter = new CustomAdapter(context, tableContent);
        cinemaRecord.setAdapter(adapter);

        ArrayList<String> movieContent;
//        When user presses edit, open the edit cinema fragment
        Button editBtn = binding.editBtn;
        binding.editBtn.setOnClickListener(v -> {
            ArrayList<String> cinemaContent = mydManger.onlyCinema();
            Edit(cinemaContent);
        });

    // When user presses delete, delete cinema row
        Button deleteBtn = binding.deleteBtn;
        deleteBtn.setOnClickListener(v -> {
            Delete(tableContent);
        });

        return root;
    }


    private void Delete(ArrayList<String> cinemaTable) {
        String cinemaRow;
        boolean isSelected = false;
        boolean[] checkboxes = adapter.getCheckBoxState();
        for (int i = 0; i < checkboxes.length; i++) {
//            Check if cinema record has been selected
            if (checkboxes[i]) {
                isSelected = true;
                cinemaRow = cinemaTable.get(i);
                String[] parts = cinemaRow.split(",");
                Log.d("cinema id", parts[0]);
                // Delete selected records from database
                mydManger.deleteCinema(parts[0]);
            }
        }
        Context applicationContext = getActivity().getApplicationContext();
        if (checkboxes.length > 0) {
            // If nothing has been selected, let user know
            if (!isSelected) {
                Toast.makeText(applicationContext, "Please select a cinema to delete.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(applicationContext, "There are no cinemas to delete.", Toast.LENGTH_SHORT).show();
        }
//        Update custom adapter
        adapter.notifyDataSetChanged();
    }

//    Update cinema row
        public void Edit(ArrayList<String> cinemaTable) {
            Boolean isSelected = false;
            String cinemaRow;
            String cinemaDetails[];
            boolean[] checkboxes = adapter.getCheckBoxState();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i]) {
                    isSelected = true;
                    cinemaRow = cinemaTable.get(i);
                    cinemaDetails = cinemaRow.split(",");
                    for (int j = 0; j < cinemaDetails.length; j++) {
//                        trim any trailing whitespace
                        cinemaDetails[j] = cinemaDetails[j].trim();
                    }

                    // Perform the update operation for selected items
                    mydManger.getCinemaDetails(cinemaDetails);

//                    Collect and store arguments to pass on to the next fragment
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("cinemaDetails", cinemaDetails);

                    EditCinema editCinemaFragment = new EditCinema();
                    editCinemaFragment.setArguments(bundle);
                    // Navigate to the edit cinema fragment
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, editCinemaFragment);
//                   Allow user to navigate back here
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            Context applicationContext = getActivity().getApplicationContext();

            if (checkboxes.length > 0) {
                // If no cinema has been selected
                if (!isSelected) {
                    Toast.makeText(applicationContext, "Please select a cinema to edit.", Toast.LENGTH_SHORT).show();
                }
            }
            //      If there are no cinemas in the database
            else {
                Toast.makeText(applicationContext, "There are no cinemas to edit.", Toast.LENGTH_SHORT).show();
            }
//            Update custom adapter
            adapter.notifyDataSetChanged();
        }
    }




