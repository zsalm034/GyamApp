package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GymMemberPage extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    private TextView welcomeText;
    private TextView noClassesMessage;
    private ListView menuOptions;
    private ImageView showMenu;
    private ImageView showProfile;
    private NavigationView sideNav;
    private ScrollView registeredClassesView;
    private ListView classList;
    private LinearLayout enrolledClassLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private LinearLayout enrolledClassInfoLayout;
    private TextView classNameText;
    private TextView classDayText;
    private TextView classTimeText;
    private TextView classInstructorText;
    private TextView classMaxCapText;
    private TextView difficultyLabel;
    private TextView difficultyText;
    private Button unenrollButton;
    private ListView profileOptions;
    private NavigationView profileOptionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_member_page);

        welcomeText = findViewById(R.id.welcomeMessageMember);
        noClassesMessage = findViewById(R.id.noClassesMessage);
        menuOptions = findViewById(R.id.navMenuOptions);
        showMenu = findViewById(R.id.topLeftIcon);
        showProfile = findViewById(R.id.topRightIcon);
        sideNav = findViewById(R.id.sideNav);
        registeredClassesView = findViewById(R.id.registeredClasses);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        classList = findViewById(R.id.enrolledClasses);
        classNameText = findViewById(R.id.registeredClassName);
        classDayText = findViewById(R.id.registeredClassDay);
        classInstructorText = findViewById(R.id.registeredClassinstructor);
        classTimeText = findViewById(R.id.registeredClassTime);
        classMaxCapText = findViewById(R.id.registeredClassMaxCap);
        difficultyLabel = findViewById(R.id.difficultyLabel);
        difficultyText = findViewById(R.id.difficultyText);
        unenrollButton = findViewById(R.id.unenrollButton);
        enrolledClassInfoLayout = findViewById(R.id.enrolledClassInfo);
        enrolledClassLayout = findViewById(R.id.enrolledClassesLayout);
        profileOptions = findViewById(R.id.profileOptionsList);
        profileOptionsLayout = findViewById(R.id.profileOptionsMenu);
        ScheduledClass.here[0] = false;


        String userName = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        String message = "Welcome "+userName+"! You are logged in as an "+role;

        welcomeText.setText(message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeText.setVisibility(View.INVISIBLE);
            }
        }, SPLASH_TIME_OUT);

        ArrayList<String> options = new ArrayList<>();
        final ArrayList<String>[] classID = new ArrayList[]{new ArrayList<>()};
        final String[] selectedClassID = new String[1];
        options.add("Available Classes");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GymMemberPage.this, R.layout.nav_menu_options, options);
        menuOptions.setAdapter(arrayAdapter);

        menuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Intent classesIntent = new Intent(getApplicationContext(), GymClasses.class);
                    classesIntent.putExtra("role", role);
                    classesIntent.putExtra("name", userName);
                    startActivity(classesIntent);
                    sideNav.setVisibility(View.GONE);
                }
            }
        });

        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = sideNav.getVisibility()==View.VISIBLE? View.GONE:View.VISIBLE;
                sideNav.setVisibility(state);
                profileOptionsLayout.setVisibility(View.GONE);
            }
        });

        //Profile Menu Options
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> profileMenuAdapter = new ArrayAdapter<>(GymMemberPage.this, R.layout.nav_menu_options, arrayList);
        profileOptions.setAdapter(profileMenuAdapter);
        profileMenuAdapter.add("Profile");
        profileMenuAdapter.add("Sign out");

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideNav.setVisibility(View.GONE);
                int state = profileOptionsLayout.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE;
                profileOptionsLayout.setVisibility(state);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(GymMemberPage.this);
        builder.setMessage("Are you sure you want to sign out ?")
                .setTitle("Sign out").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        Intent loginIntent = new Intent(GymMemberPage.this, MainActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();

        profileOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){

                }
                else{
                    dialog.show();
                }
            }
        });

        DatabaseReference classesRef = mRef.child("users").child(mUser.getUid()).child("gymClasses").getRef();

        classesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    noClassesMessage.setVisibility(View.VISIBLE);
                    registeredClassesView.setVisibility(View.GONE);
                }
                else {
                    ArrayList<String> classNames = new ArrayList<>();
                    ArrayList<String> instructorNames = new ArrayList<>();
                    ArrayList<String> datesAndTimes = new ArrayList<>();

                    String className;
                    classID[0] = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        classID[0].add(dataSnapshot.getKey().toString());
                        if (dataSnapshot.hasChild("startTime")&&dataSnapshot.hasChild("endTime")
                                && dataSnapshot.hasChild("maximumCapacity") && dataSnapshot.hasChild("day")
                                && dataSnapshot.hasChild("instructor") && dataSnapshot.hasChild("classType")) {
                            String startTime = dataSnapshot.child("startTime").getValue().toString();

                            String endTime = dataSnapshot.child("endTime").getValue().toString();
                            String maxCap = dataSnapshot.child("maximumCapacity").getValue().toString();
                            String day = dataSnapshot.child("day").getValue().toString();
                            String instructorName = dataSnapshot.child("instructor").getChildren().iterator().next().getValue().toString();
                            className = dataSnapshot.child("classType").getValue().toString();

                            classNames.add(className);
                            instructorNames.add(instructorName);

                            String period = day.substring(0, 1).toUpperCase() + day.substring(1, 3) + ". " + startTime + " - " + endTime;
                            datesAndTimes.add(period);
                        }
                        CustomClassList customClassList = new CustomClassList(GymMemberPage.this, classNames, datesAndTimes, instructorNames);
                        classList.setAdapter(customClassList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedClassID[0] = classID[0].get(position);
                mRef.child("gymClass").child(parent.getItemAtPosition(position).toString()).child(selectedClassID[0]).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            classNameText.setText(snapshot.child("className").getValue().toString());

                            String startTime = snapshot.child("startTime").getValue().toString();
                            String endTime = snapshot.child("endTime").getValue().toString();
                            classDayText.setText(snapshot.child("day").getValue().toString());
                            classTimeText.setText(startTime + " - " + endTime);
                            classMaxCapText.setText(snapshot.child("maximumCapacity").getValue().toString());
                            difficultyText.setText(snapshot.child("difficulty").getValue().toString());
                            String instructorName = snapshot.child("instructor").child("name").getValue().toString();
                            classInstructorText.setText(instructorName);

                            crossFade(enrolledClassInfoLayout, View.VISIBLE);
                            enrolledClassInfoLayout.setAlpha(0.3F);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        AlertDialog.Builder builder1 = new AlertDialog.Builder(GymMemberPage.this);
        builder1.setMessage("Are you sure you want to unenroll from this class ?")
                .setTitle("Unenroll from class").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final boolean[] clicked = {true};
                        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                completeUnenrollAction(clicked, snapshot, selectedClassID);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog1 = builder1.create();

        unenrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
            }
        });

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

    public void completeUnenrollAction(boolean[] clicked, DataSnapshot snapshot, String[] selectedClassID){
        if (clicked[0]) {
            snapshot.child(selectedClassID[0]).getRef().removeValue();
            mRef.child("gymClass").child(classNameText.getText().toString()).child(selectedClassID[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (clicked[0]) {
                        long currentUsers = Integer.parseInt(snapshot.child("numberOfUsers").getValue().toString());
                        snapshot.child("numberOfUsers").getRef().setValue(currentUsers - 1);
                        String instructorID = snapshot.child("instructor").child("userID").getValue().toString();
                        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(instructorID).getRef();
                        tempRef.child("gymClasses").child(selectedClassID[0]).child("numberOfUsers").setValue(currentUsers-1);
                        tempRef.child("gymClasses").child(selectedClassID[0]).child("members").child(mUser.getUid()).getRef().removeValue();

                        DatabaseReference tempRef2 = FirebaseDatabase.getInstance().getReference().child("gymClass").child(classNameText.getText().
                                toString()).child(selectedClassID[0]).getRef();
                        tempRef2.child("members").child(mUser.getUid()).getRef().removeValue();

                        clicked[0] = false;
                        finish();
                        startActivity(getIntent());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (enrolledClassInfoLayout.getVisibility()==View.VISIBLE){
                crossFade(enrolledClassInfoLayout, View.GONE);
                showProfile.setClickable(true);
                showMenu.setClickable(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}