package com.example.testgymapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CustomClassListV2 extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> className;
    private final ArrayList<String> description;

    public CustomClassListV2(@NonNull Activity context1, ArrayList<String> className, ArrayList<String> description) {
        super(context1, R.layout.gym_class_item, className);
        this.context = context1;
        this.className = className;
        this.description = description;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.gym_class_item, null,true);

        TextView main = rowView.findViewById(R.id.textView11);
        TextView details = rowView.findViewById(R.id.textView12);

        main.setText(className.get(position));
        details.setText(description.get(position));

        return rowView;
    }
}
