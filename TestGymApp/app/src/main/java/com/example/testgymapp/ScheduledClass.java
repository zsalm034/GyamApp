package com.example.testgymapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduledClass extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private ListView availableList;
    private LinearLayout availableClassesLayout;
    private LinearLayout classInfoLayout;
    private TextView className;
    private TextView day;
    private TextView time;
    private TextView numberOfUsers;
    private TextView maxCap;
    private TextView instructorNameView;
    private TextView difficultyText;
    private Button completeEnrol;
    private EditText searchField;
    private ImageView searchButton;
    private ImageView backButton;
    public static final boolean[] here = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_class);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        classInfoLayout = findViewById(R.id.classInfo);
        availableList = findViewById(R.id.availableClasses);
        className = findViewById(R.id.actualClassName);
        day = findViewById(R.id.classDay);
        time = findViewById(R.id.classTime);
        numberOfUsers = findViewById(R.id.numberOfUsers);
        maxCap = findViewById(R.id.maxCap);
        difficultyText = findViewById(R.id.difficultyText1);
        instructorNameView = findViewById(R.id.classInstructor);
        completeEnrol = findViewById(R.id.confirmEnroll);
        availableClassesLayout = findViewById(R.id.availableClassesLayout);
        searchField = findViewById(R.id.classSearchField);
        searchButton = findViewById(R.id.scheduleClassSearchButton);
        backButton = findViewById(R.id.backIcon);

        String classType = getIntent().getStringExtra("classType");
        String role = getIntent().getStringExtra("role");
        String userName = getIntent().getStringExtra("name");

        DatabaseReference classRef = mRef.child("gymClass").child(classType).getRef();
        final ArrayList<String>[] classID = new ArrayList[]{new ArrayList<>()};
        final String[] selectedClassID = new String[1];
        final String[] classInstructor = new String[1];
        final boolean[] hasBeenIncremented = {false};
        final String[] instructorID = new String[1];
        here[0] = true;

        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("classes", snapshot.toString());
                classID[0]= setClickedItem(classType, snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        availableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hasBeenIncremented[0] =false;
                    selectedClassID[0] = classID[0].get(position);
                    mRef.child("gymClass").child(parent.getItemAtPosition(position).toString()).child(selectedClassID[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            className.setText(snapshot.child("className").getValue().toString());

                            instructorID[0] = snapshot.child("instructor").child("userID").getValue().toString();
                            classInstructor[0] = snapshot.child("instructor").child("name").getValue().toString();
                            String startTime = snapshot.child("startTime").getValue().toString();
                            String endTime = snapshot.child("endTime").getValue().toString();
                            String temp = snapshot.child("day").getValue().toString();
                            day.setText(temp.substring(0, 1).toUpperCase()+temp.substring(1));
                            time.setText(startTime+" - "+endTime);
                            maxCap.setText(snapshot.child("maximumCapacity").getValue().toString());
                            difficultyText.setText(snapshot.child("difficulty").getValue().toString());
                            numberOfUsers.setText(snapshot.child("numberOfUsers").getValue().toString());
                            String instructorName = snapshot.child("instructor").child("name").getValue().toString();
                            instructorNameView.setText(instructorName);

                            crossFade(classInfoLayout, View.VISIBLE);
                            availableClassesLayout.setAlpha(0.3F);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        completeEnrol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = mUser.getUid();
                    final boolean[] clicked = {true};
                    mRef.child("gymClass").child(classType).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (!snapshot1.exists()||!clicked[0]){

                            }
                            else {
                                final boolean[] canEnroll = {true};
                                String classDay = day.getText().toString();
                                String[] times = time.getText().toString().split("-");
                                long maxCapOfUsers = Integer.parseInt(maxCap.getText().toString());
                                long currentNumOfUsers = Integer.parseInt(numberOfUsers.getText().toString());
                                String startPeriod = times[0].trim();
                                String endPeriod = times[1].trim();
                                long start = timeConv(startPeriod);
                                long end = timeConv(endPeriod);
                                if (currentNumOfUsers>=maxCapOfUsers){
                                    Toast.makeText(ScheduledClass.this, "The maximum capacity of users has been reached. Sorry!", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                mRef.child("users").child(uid).child("gymClasses").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot classes: snapshot.getChildren()) {
                                            if (classes.hasChild("day") && classes.hasChild("startTime") &&
                                                    classes.hasChild("endTime")) {
                                                if (classDay.equals(classes.child("day").getValue().toString())) {
                                                    long classStart = timeConv(classes.child("startTime").getValue().toString());
                                                    long classEnd = timeConv(classes.child("endTime").getValue().toString());

                                                    if (start >= classStart && start <= classEnd) {
                                                        canEnroll[0] = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (canEnroll[0]&&!hasBeenIncremented[0]&&here[0]){
                                            DatabaseReference myRef = mRef.child("users").child(uid).child("gymClasses").child(selectedClassID[0]).getRef();
                                            myRef.child("classType").getRef().setValue(className.getText().toString());
                                            myRef.child("day").getRef().setValue(day.getText().toString());
                                            myRef.child("startTime").getRef().setValue(startPeriod);
                                            myRef.child("endTime").getRef().setValue(endPeriod);
                                            myRef.child("maximumCapacity").getRef().setValue(Integer.parseInt(maxCap.getText().toString()));
                                            myRef.child("instructor").child("name").setValue(instructorNameView.getText().toString());

                                            long users = Integer.parseInt(snapshot1.child(selectedClassID[0]).child("numberOfUsers").getValue().toString())+1;

                                            DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(instructorID[0]).getRef();
                                            tempRef.child("gymClasses").child(selectedClassID[0]).child("numberOfUsers").setValue(users);
                                            tempRef.child("gymClasses").child(selectedClassID[0]).child("members").child(mUser.getUid()).setValue(userName);

                                            snapshot1.child(selectedClassID[0]).child("numberOfUsers").getRef().setValue(users);
                                            snapshot1.child(selectedClassID[0]).child("members").child(mUser.getUid()).getRef().setValue(userName);

                                            canEnroll[0] = false;
                                            hasBeenIncremented[0]=true;
                                            clicked[0] =false;
                                            classInfoLayout.setVisibility(View.GONE);
                                            availableClassesLayout.setAlpha(1);
                                            Intent backIntent = new Intent(getApplicationContext(), GymMemberPage.class);
                                            backIntent.putExtra("role", "Member");
                                            backIntent.putExtra("name", userName);
                                            startActivity(backIntent);
                                            finish();
                                        }
                                        else if (!canEnroll[0]&&!hasBeenIncremented[0]&&here[0]){
                                            Toast.makeText(ScheduledClass.this, "Time Conflict. Cannot Enroll", Toast.LENGTH_LONG).show();
                                            clicked[0] =false;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
                s = s.toString().toLowerCase();

                classRef.orderByChild("day").startAt(s.toString()).endAt(s.toString()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
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

                            CustomClassList customClassList = new CustomClassList(ScheduledClass.this, classNames, datesAndTimes, instructorNames);
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

            String period = day.substring(0, 1).toUpperCase() + day.substring(1, 3)+". "+startTime+" - "+endTime;
            datesAndTimes.add(period);
        }
        CustomClassList customClassList = new CustomClassList(ScheduledClass.this, classNames, datesAndTimes, instructorNames);
        availableList.setAdapter(customClassList);
        return classID;
    }

    public void crossFade(View view, int visibility){
        int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        view.setAlpha(0f);
        view.setVisibility(visibility);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        view.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }
    public int timeConv(String time){
        String[] times = time.split(" ");
        int hours = Integer.parseInt(times[0].replace(":",""));
        if(times[1].equals("am") || times[0].startsWith("12")){
            return hours;
        }else{
            return hours+1200;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (classInfoLayout.getVisibility()==View.VISIBLE){
                crossFade(classInfoLayout, View.GONE);
                availableClassesLayout.setAlpha(1.0F);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}