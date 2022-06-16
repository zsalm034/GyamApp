package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduledClassesInstructor extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private ListView availableList;
    private LinearLayout availableClassesLayout;
    private LinearLayout classInfoLayout;
    private EditText searchField;
    private ImageView searchButton;
    private ImageView backButton;
    public static final boolean[] here = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_classes_instructor);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        classInfoLayout = findViewById(R.id.classInfo);
        availableList = findViewById(R.id.availableClassesInstr);
        availableClassesLayout = findViewById(R.id.availableClassesLayout);
        searchField = findViewById(R.id.classSearchField);
        searchButton = findViewById(R.id.scheduleClassSearchButton);
        backButton = findViewById(R.id.backIcon);

        String classType = getIntent().getStringExtra("classType");
        String role = getIntent().getStringExtra("role");

        DatabaseReference classRef = mRef.child("gymClass").child(classType).getRef();
        final ArrayList<String>[] classID = new ArrayList[]{new ArrayList<>()};
        here[0] = true;

        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classID[0]= setClickedItem(classType, snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classID[0] = new ArrayList<>();


                classRef.orderByChild("instructor/name").startAt(s.toString()).endAt(s.toString()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            classID[0] = setClickedItem(classType, snapshot);
                        }
                        else {
                            ArrayList<String> classNames = new ArrayList<>();
                            ArrayList<String> instructorNames = new ArrayList<>();
                            ArrayList<String> datesAndTimes = new ArrayList<>();
                            classID[0] = new ArrayList<>();

                            CustomClassList customClassList = new CustomClassList(ScheduledClassesInstructor.this, classNames, datesAndTimes, instructorNames);
                            availableList.setAdapter(customClassList);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            }
        });
    }

    public ArrayList<String> setClickedItem(String classType, DataSnapshot snapshot){
        ArrayList<String> classNames = new ArrayList<>();

        ArrayList<String> instructorNames = new ArrayList<>();
        ArrayList<String> datesAndTimes = new ArrayList<>();
        ArrayList<String> classID = new ArrayList<>();

        for (DataSnapshot classes:snapshot.getChildren()){
            classID.add(classes.getKey().toString());
            classNames.add(classType);
            String startTime = classes.child("startTime").getValue().toString();
            String endTime = classes.child("endTime").getValue().toString();
            String day = classes.child("day").getValue().toString();
            String instructorName = classes.child("instructor").child("name").getValue().toString();

            instructorNames.add(instructorName);

            String period = day.substring(0, 1).toUpperCase()+day.substring(1, 3)+". "+startTime+" - "+endTime;
            datesAndTimes.add(period);
        }
        CustomClassList customClassList = new CustomClassList(ScheduledClassesInstructor.this, classNames, datesAndTimes, instructorNames);
        availableList.setAdapter(customClassList);
        return classID;
    }
}