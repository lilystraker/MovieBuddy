package com.example.moviebuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private boolean checked = false;

//    Store whether each checkbox has been selected or not
    boolean[] checkBoxState;

    ViewHolder viewHolder;

    public CustomAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.movie_list, values);
        this.context = context;
        this.values = values;
        checkBoxState = new boolean[values.size()];
    }

    private class ViewHolder {
        TextView name;
        CheckBox checkBox;

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getName() {
            return name;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_list, null);
            viewHolder = new ViewHolder();

            viewHolder.name = convertView.findViewById(R.id.label);
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.name.setText(values.get(position));

        String s = values.get(position);
//        Set the state of the checkbox in the array
        viewHolder.checkBox.setChecked(checkBoxState[position]);

//        Respond if user checks or unchecks a box
        viewHolder.checkBox.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) {
                checkBoxState[position] = true;
            } else
                checkBoxState[position] = false;
        });

        return convertView;
    }

    public boolean[] getCheckBoxState() {
        return checkBoxState;
    }

    public void setCheckBoxState(int position, boolean isChecked) {
        checkBoxState[position] = isChecked;
        notifyDataSetChanged(); // Notify adapter to refresh views
    }

    public String getName(int pos) {
        String val = values.get(pos);
        return val;
    }
}

