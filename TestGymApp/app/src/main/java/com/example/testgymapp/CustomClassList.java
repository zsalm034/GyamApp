package com.example.testgymapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CustomClassList extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> className;
    private final ArrayList<String> dateAndTime;
    private final ArrayList<String> instructor;

    public CustomClassList(@NonNull Activity context1, ArrayList<String> className, ArrayList<String> dateAndTime, ArrayList<String> instructor) {
        super(context1, R.layout.registered_classes_list_item, className);
        this.context = context1;
        this.className = className;
        this.dateAndTime = dateAndTime;
        this.instructor = instructor;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.registered_classes_list_item, null,true);

        TextView main = rowView.findViewById(R.id.shortName);
        TextView date = rowView.findViewById(R.id.shortDate);
        TextView theInstructor = rowView.findViewById(R.id.shortInstructor);

        main.setText(className.get(position));
        date.setText(dateAndTime.get(position));
        theInstructor.setText(instructor.get(position));

        return rowView;
    }
}
