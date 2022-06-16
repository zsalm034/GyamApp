package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.TestLooperManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InstructorPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private TextView welcomeText;
    private TextView noClassesMessage;
    private EditText classNameField;
    private ListView classList;
    private ArrayList<String> className;
    private Spinner day, startTime, endTime, difficulty;
    private Button endClassEdit, deleteClassButton;
    private EditText maxCap;
    private static int SPLASH_TIME_OUT = 2000;
    private ListView profileOptions;
    private NavigationView profileOptionsLayout;
    private NavigationView sideNav;
    private ListView menuOptions;
    private ImageView showMenu;
    private ImageView showProfile;
    private ScrollView instructorClass;
    private String id;
    private DatabaseReference mRef2;
    private ScrollView registeredClassesView;
    private Button viewUsers;
    static  ArrayList<String> usersList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_page);

        welcomeText = findViewById(R.id.welcomeMessageInstructor);
        noClassesMessage = findViewById(R.id.noClassesMessageInstructor);

        classNameField = findViewById(R.id.classNameField);
        classList = findViewById(R.id.classList);

        showProfile = findViewById(R.id.topRightIcon);
        showMenu= findViewById(R.id.topLeftIcon);
        menuOptions = findViewById(R.id.navMenuOptions);
        sideNav = findViewById(R.id.sideNav);
        profileOptionsLayout = findViewById(R.id.profileOptionsMenu);
        profileOptions = findViewById(R.id.profileOptionsList);
        maxCap = findViewById(R.id.maxCap);
        instructorClass = findViewById(R.id.instructorClass);
        endClassEdit = findViewById(R.id.endClassEdit);
        deleteClassButton = findViewById(R.id.deleteClassButton);
        registeredClassesView = findViewById(R.id.registeredClassesInst);
        viewUsers = findViewById(R.id.seeUsersButton);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        id = mUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef2 = FirebaseDatabase.getInstance().getReference().child("gymClass");

        day = findViewById(R.id.classDay);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(adapter1);

        startTime = findViewById(R.id.startTime);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTime.setAdapter(adapter2);

        endTime = findViewById(R.id.endTime);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTime.setAdapter(adapter3);

        difficulty = findViewById(R.id.classDifficulty);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.difficulty, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficulty.setAdapter(adapter4);

        String userName = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        String message = "Welcome "+userName+"! You are logged in as an "+role;

//        className = new ArrayList<>();
        final String[] dbName = new String[1];
        final String[] nameValue = new String[1];
        final String[] clashingInstructor = new String[1];

        welcomeText.setText(message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeText.setVisibility(View.INVISIBLE);
            }
        }, SPLASH_TIME_OUT);

        ArrayList<String> options = new ArrayList<>();
        options.add("Available Classes");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.nav_menu_options, options);
        menuOptions.setAdapter(arrayAdapter);

        menuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0){
                    Intent classesIntent = new Intent(getApplicationContext(), GymClasses.class);
                    classesIntent.putExtra("role", role);
                    classesIntent.putExtra("name", userName);
                    startActivity(classesIntent);
                }
                sideNav.setVisibility(View.GONE);
            }
        });

        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = sideNav.getVisibility()==View.VISIBLE? View.GONE:View.VISIBLE;
                sideNav.setVisibility(state);
                crossFade(sideNav, state);
                profileOptionsLayout.setVisibility(View.GONE);
            }
        });

        //Profile Menu Options
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> profileMenuAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.nav_menu_options, arrayList);
        profileOptions.setAdapter(profileMenuAdapter);
        profileMenuAdapter.add("Profile");
        profileMenuAdapter.add("Sign out");

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideNav.setVisibility(View.GONE);
                int state = profileOptionsLayout.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE;
                crossFade(profileOptionsLayout, state);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(InstructorPage.this);
        builder.setMessage("Are you sure you want to sign out ?")
                .setTitle("Sign out").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        Intent loginIntent = new Intent(InstructorPage.this, MainActivity.class);
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

        mRef.child(id).child("gymClasses").addValueEventListener(new ValueEventListener() {
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

                    CustomClassList customClassList = new CustomClassList(InstructorPage.this, classNames, datesAndTimes, instructorNames);
                    classList.setAdapter(customClassList);

                    for (DataSnapshot classes : snapshot.getChildren()) {
                        String className = classes.child("className").getValue().toString();
                        String day = classes.child("day").getValue().toString();
                        String startTime = classes.child("startTime").getValue().toString();
                        String endTime = classes.child("endTime").getValue().toString();
                        String period = day.substring(0, 1).toUpperCase() +day.substring(1, 3) + ". " + startTime + " - " + endTime;

                        classNames.add(className);
                        datesAndTimes.add(period);
                        instructorNames.add(userName);
                    }

                    customClassList.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                TextView listClassName = (TextView) view.findViewById(R.id.shortName);
                TextView dayAndTime = (TextView) view.findViewById(R.id.shortDate);

                String[] values = new String[2];
                values[0] = listClassName.getText().toString();
                values[1] = dayConv(dayAndTime.getText().toString().split("\\.")[0]).toLowerCase();

                mRef.child(id).child("gymClasses").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            for (DataSnapshot classID: task.getResult().getChildren()){
                                if(values[0].equals(classID.child("className").getValue().toString().toLowerCase()) && values[1].equals(classID.child("day").getValue().toString().toLowerCase())){

                                    nameValue[0] = values[0];
                                    String days = classID.child("day").getValue().toString();
                                    String start = classID.child("startTime").getValue().toString();
                                    String end = classID.child("endTime").getValue().toString();
                                    String diff = classID.child("difficulty").getValue().toString();
                                    String maxCapacity = classID.child("maximumCapacity").getValue().toString();
                                    dbName[0]= nameValue[0] + "_" + userName + "_" + days + "_" + start + "_" + end;
                                    days = days.substring(0, 1).toUpperCase() + days.substring(1);

                                    ArrayAdapter daySpinner = (ArrayAdapter) day.getAdapter();
                                    int spinnerPos = daySpinner.getPosition(days);
                                    day.setSelection(spinnerPos);

                                    ArrayAdapter startSpinner = (ArrayAdapter) startTime.getAdapter();
                                    spinnerPos = startSpinner.getPosition(start);
                                    startTime.setSelection(spinnerPos);

                                    ArrayAdapter endSpinner = (ArrayAdapter) endTime.getAdapter();
                                    spinnerPos = endSpinner.getPosition(end);
                                    endTime.setSelection(spinnerPos);

                                    ArrayAdapter diffSpinner = (ArrayAdapter) difficulty.getAdapter();
                                    spinnerPos = diffSpinner.getPosition(diff);
                                    difficulty.setSelection(spinnerPos);

                                    usersList = new ArrayList<>();
                                    for (DataSnapshot dataSnapshot : classID.child("members").getChildren()){
                                        usersList.add(dataSnapshot.getValue().toString());

                                    }

                                    maxCap.setText(maxCapacity);

                                    classList.setVisibility(View.GONE);
                                    instructorClass.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }
        });

        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference gRef = FirebaseDatabase.getInstance().getReference().child("gymClass");
                DatabaseReference gRef2 = FirebaseDatabase.getInstance().getReference().child("gymClass").
                        child(nameValue[0]).child(dbName[0]);

                ArrayList<String> ids = new ArrayList<>();
                gRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.child("members").getChildren()){
//                            Log.d("user", ds.toString());
                            ids.add(ds.getKey());
                        }

                        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().
                                child("users");

                        for (String s : ids){
                            usersRef = usersRef.child(s).child("gymClasses").child(dbName[0]);
//                            Log.d("classs", usersRef.toString());
                            usersRef.removeValue();
                        }
                        gRef.child(nameValue[0]).child(dbName[0]).removeValue();
                        mRef.child(id).child("gymClasses").child(dbName[0]).removeValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                classList.setVisibility(View.VISIBLE);
                instructorClass.setVisibility(View.GONE);
            }
        });

        endClassEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean[] clicked = {true};

                DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference();

                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!clicked[0]){

                        }
                        else{
                            String d = snapshot.child("gymClassType").child(nameValue[0]).child("description").getValue().toString();
                            String start = startTime.getSelectedItem().toString();
                            String end = endTime.getSelectedItem().toString();
                            String days = day.getSelectedItem().toString().toLowerCase();
                            String diff = difficulty.getSelectedItem().toString();

                            int s1 = timeConv(start);
                            int e1 = timeConv(end);

                            String name = snapshot.child("users").child(id).child("name").getValue().toString();
                            String email = snapshot.child("users").child(id).child("email").getValue().toString();
                            Instructor tmp = new Instructor(name, email, id);
                            if (s1 >= e1) {
                                Toast.makeText(getApplicationContext(), "You have entered an invalid time frame, please select a valid time frame", Toast.LENGTH_LONG).show();
                                return;
                            }

                            myRef2.child("gymClass").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final boolean[] noTimeConf = {true};
                                    final boolean[] noNameConf = {true};
//                                    int s = timeConv(start);
//                                    int e = timeConv(end);
                                    for (DataSnapshot classID : snapshot.getChildren()) {
                                        for (DataSnapshot classes : classID.getChildren()) {
//                                            int s2 = timeConv(classes.child("startTime").getValue().toString());
//                                            int e2 = timeConv(classes.child("endTime").getValue().toString());
                                            if (days.equals(classes.child("day").getValue().toString().toLowerCase())) {
                                                if (nameValue[0].equals(classes.child("className").getValue().toString()) &&
                                                        !userName.equals(classes.child("instructor").child("name").getValue().toString())) {
                                                    noNameConf[0] = false;
                                                    clashingInstructor[0] = classes.child("instructor").child("name").getValue().toString();
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                    if(noNameConf[0] && verifyMaxCap()){
                                        int cap = Integer.parseInt(maxCap.getText().toString());
                                        String classID = nameValue[0] + "_" + name + "_" + days + "_" + start + "_" + end;

                                        DatabaseReference gRef = FirebaseDatabase.getInstance().getReference().child("gymClass");
                                        DatabaseReference gRef2 = FirebaseDatabase.getInstance().getReference().child("gymClass");

                                        ArrayList<String> ids = new ArrayList<>();
                                        ArrayList<String> names = new ArrayList<>();

                                        gRef2.child(nameValue[0]).child(dbName[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for (DataSnapshot ds : snapshot.child("members").getChildren()){
                                                    ids.add(ds.getKey());
                                                    names.add(ds.getValue().toString());
                                                }

                                                gRef.child(nameValue[0]).child(dbName[0]).removeValue();
                                                mRef.child(id).child("gymClasses").child(dbName[0]).removeValue();

                                                GymClass gymClass = new GymClass(nameValue[0], d, start, end, cap, days, diff, tmp);

                                                myRef2.child("gymClass").child(nameValue[0]).child(classID).setValue(gymClass);
                                                for (int i=0; i<ids.size(); i++){
                                                    String tempId= ids.get(i);
                                                    String tempNames = names.get(i);
                                                    myRef2.child("gymClass").child(nameValue[0]).child(classID).child(tempId).setValue(tempNames);
                                                }

                                                mRef.child(id).child("gymClasses").child(classID).setValue(gymClass);
                                                for (int i=0; i<ids.size(); i++){
                                                    String tempId= ids.get(i);
                                                    String tempNames = names.get(i);
                                                    mRef.child("gymClass").child(nameValue[0]).child(classID).child(tempId).setValue(tempNames);
                                                }

                                                clicked[0] = false;

                                                classList.setVisibility(View.VISIBLE);
                                                instructorClass.setVisibility(View.GONE);

                                                Intent classes = new Intent(InstructorPage.this, InstructorPage.class);
                                                classes.putExtra("name", userName);
                                                classes.putExtra("role", role);
                                                startActivity(classes);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }

                                    else if (!noNameConf[0]) {
                                        Toast.makeText(getApplicationContext(), "The day you have chosen conflicts with another class of the same type" +
                                                " scheduled by instructor "+clashingInstructor[0]+", please select another day", Toast.LENGTH_LONG).show();
                                        clicked[0] = false;
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

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstructorPage.this, RegirsteredUsers.class);
                intent.putExtra("role", role);
                intent.putExtra("name", userName);
                startActivity(intent);
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
    public boolean verifyMaxCap(){
        maxCap = (EditText) findViewById(R.id.maxCap);
        String cap = maxCap.getText().toString().trim();
        try{
            int max = Integer.parseInt(cap);
            if(max < 0){
                maxCap.setError("Entered invalid class capacity");
                return false;
            }
            if(max > 50){
                maxCap.setError("Class capacity must not exceed 50 members");
                return false;
            }

        }
        catch (NumberFormatException e){
            maxCap.setError("Entered invalid capacity");
            return false;
        }

        return true;
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
    public String dayConv(String day){
        switch (day){
            case "Mon":
                return "Monday";
            case "Tue":
                return "Tuesday";
            case "Wed":
                return "Wednesday";
            case "Thu":
                return "Thursday";
            case "Fri":
                return "Friday";
            case "Sat" :
                return "Saturday";
            default:
                return "Sunday";
        }
    }
}