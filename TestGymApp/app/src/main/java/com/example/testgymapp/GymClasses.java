package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GymClasses extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mdb;
    private ListView myGymClasses;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private Button completeEdit;
    private EditText className;
    private EditText classDescription;
    private EditText maxCap;
    private DatabaseReference myRef;
    private LinearLayout editClassWin;
    private LinearLayout instructorClass;
    private Button deleteButton;
    private Button endClassCreate;
    private Button exit;
    private Spinner day;
    private Spinner startTime;
    private Spinner endTime;
    private Spinner difficulty;
    private ScrollView scrollView;
    private EditText classSearch;
    private ImageView searchButton;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_classes);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myGymClasses = findViewById(R.id.classList1);
        className = findViewById(R.id.editClassName);
        classDescription = findViewById(R.id.editClassDescription);
        editClassWin = findViewById(R.id.editClassWindow);
        completeEdit = findViewById(R.id.completeClassEdit);
        deleteButton = findViewById(R.id.deleteClassButton);
        endClassCreate = findViewById(R.id.endClassCreate);
        instructorClass = findViewById(R.id.instructorClass);
        scrollView = findViewById(R.id.scrollView3);
        classSearch = findViewById(R.id.classSearch);
        searchButton= findViewById(R.id.classTypeSearchButton);
        backIcon = findViewById(R.id.backIcon);

        maxCap = findViewById(R.id.maxCap);
        exit = findViewById(R.id.exit);

        myRef = FirebaseDatabase.getInstance().getReference().child("gymClassType");
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        final String[] actualName = new String[1];
        final String[] desc = new String[1];
        String role = getIntent().getStringExtra("role");
        String userName = getIntent().getStringExtra("name");
        final String[] tempValue = new String[1];
        final String[] clashingInstructor = new String[1];

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


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<String> nameList = new ArrayList<>();
                final ArrayAdapter adapter = new ArrayAdapter<String>(GymClasses.this, R.layout.gym_class_item, nameList);
                myGymClasses.setAdapter(adapter);

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    nameList.add(dataSnapshot.child("className").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        AlertDialog.Builder builder1 = new AlertDialog.Builder(GymClasses.this);
        builder1.setMessage("Create a new class or view existing classes ?")
                .setTitle("Create or View").
                setPositiveButton("Create a New Class", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        actualName[0] = tempValue[0];
                        instructorClass.setVisibility(View.VISIBLE);
                        myGymClasses.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("View Existing Classes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(GymClasses.this, ScheduledClassesInstructor.class);
                        intent.putExtra("classType", tempValue[0]);
                        intent.putExtra("role", role);
                        intent.putExtra("name", userName);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog1 = builder1.create();

        myGymClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long  l) {
                String value = adapterView.getItemAtPosition(i).toString().toLowerCase();
                tempValue[0] = value;
                myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if(role.equals("Administrator")) {
                                actualName[0] = value;
                                String actualDescription = task.getResult().child(value).child("description").getValue().toString();
                                className.setText(actualName[0]);
                                classDescription.setText(actualDescription);
                                editClassWin.setVisibility(View.VISIBLE);
                                myGymClasses.setVisibility(View.GONE);
                                editClassWin.setClickable(true);
                            }
                            else if(role.equals("Instructor")){
                                dialog1.show();
                            }
                            else {
                                Intent intent = new Intent(GymClasses.this, ScheduledClass.class);
                                intent.putExtra("classType", tempValue[0]);
                                intent.putExtra("role", role);
                                intent.putExtra("name", userName);
                                startActivity(intent);
                            }

                        }
                    }
                });
            }
        });

        endClassCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mUser.getUid();
                final boolean[] clicked = {true};

                DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference();

                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if(!clicked[0]){

                         }
                         else {
                             String start = startTime.getSelectedItem().toString();
                             String end = endTime.getSelectedItem().toString();
                             String days = day.getSelectedItem().toString().toLowerCase();
                             String diff = difficulty.getSelectedItem().toString();

                             int s1 = timeConv(start);
                             int e1 = timeConv(end);

                             if (s1 >= e1) {
                                 Toast.makeText(getApplicationContext(), "You have entered an invalid time frame, please select a valid time frame", Toast.LENGTH_LONG).show();
                                 return;
                             }

                             String name = snapshot.child("users").child(id).child("name").getValue().toString();
                             String email = snapshot.child("users").child(id).child("email").getValue().toString();
                             Instructor tmp = new Instructor(name, email, id);

                             if (snapshot.hasChild("gymClass")) {
                                 myRef2.child("gymClass").addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         final boolean[] noNameConf = {true};
//                                         int s = timeConv(start);
//                                         int e = timeConv(end);
                                         for (DataSnapshot classID : snapshot.getChildren()) {
                                             for (DataSnapshot classes : classID.getChildren()) {
//                                                 int s2 = timeConv(classes.child("startTime").getValue().toString());
//                                                 int e2 = timeConv(classes.child("endTime").getValue().toString());
                                                 if (days.equals(classes.child("day").getValue().toString().toLowerCase())) {
                                                     if (actualName[0].equals(classes.child("className").getValue().toString())) {
                                                         noNameConf[0] = false;
                                                         clashingInstructor[0] = classes.child("instructor").child("name").getValue().toString();
                                                         break;
                                                     }
                                                 }
                                             }
                                         }
                                         if (noNameConf[0] && verifyMaxCap()) {
                                             int cap = Integer.parseInt(maxCap.getText().toString());
                                             DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("gymClass");
                                             String classID= actualName[0] + "_" + name + "_" + days + "_" + start + "_" + end;

                                             GymClass gymClass = new GymClass(actualName[0], desc[0], start, end, cap, days, diff, tmp, Integer.valueOf(0));

                                             mRef.child(id).child("gymClasses").child(classID).setValue(gymClass);

                                             newRef.child(actualName[0]).child(classID).setValue(gymClass);
                                             maxCap.setText("");
                                             instructorClass.setVisibility(View.GONE);
                                             myGymClasses.setVisibility(View.VISIBLE);
                                             clicked[0] = false;
                                             Intent back = new Intent(getApplicationContext(), InstructorPage.class);
                                             back.putExtra("name", userName);
                                             back.putExtra("role", role);
                                             startActivity(back);
                                         } else if (!noNameConf[0]) {
                                             Toast.makeText(getApplicationContext(), "The day you have chosen conflicts with another class of the same type" +
                                                     " scheduled by instructor "+clashingInstructor[0]+", please select another day", Toast.LENGTH_LONG).show();
                                             clicked[0] = false;
                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });

                             } else {
                                 if (verifyMaxCap()) {
                                     int cap = Integer.parseInt(maxCap.getText().toString());
                                     DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("gymClass");
                                     String classID = actualName[0] + "_" + name + "_" + days + "_" + start + "_" + end;

                                     GymClass gymClass = new GymClass(actualName[0], desc[0], start, end, cap, days, diff, tmp, 0);
                                     mRef.child(id).child("gymClasses").child(classID).setValue(gymClass);

                                     newRef.child(actualName[0]).child(classID).setValue(gymClass);
                                     maxCap.setText("");
                                     instructorClass.setVisibility(View.GONE);
                                     myGymClasses.setVisibility(View.VISIBLE);
                                     Intent back = new Intent(getApplicationContext(), InstructorPage.class);
                                     back.putExtra("name", userName);
                                     back.putExtra("role", role);
                                     startActivity(back);
                                 }
                             }
                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxCap.setText("");
                instructorClass.setVisibility(View.GONE);
                myGymClasses.setVisibility(View.VISIBLE);
            }
        });

        completeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GymClass gymClass = new GymClass(className.getText().toString(), classDescription.getText().toString());
                className.setText("");
                classDescription.setText("");
                editClassWin.setVisibility(View.GONE);
                myGymClasses.setVisibility(View.VISIBLE);
                editClassWin.setClickable(false);
                myRef.child(actualName[0]).removeValue();
                myRef.child(gymClass.getClassName().toLowerCase()).setValue(gymClass);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(GymClasses.this);
        builder.setMessage("Are you sure you want to delete this class ?")
                .setTitle("Delete class").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.child(actualName[0]).removeValue();
                        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("gymClass");
                        tempRef.child(actualName[0]).removeValue();
                        className.setText("");
                        classDescription.setText("");
                        editClassWin.setVisibility(View.GONE);
                        myGymClasses.setVisibility(View.VISIBLE);
                        editClassWin.setClickable(false);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        classSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> searchedClass=new ArrayList<>();
                ArrayList<String> descriptions=new ArrayList<>();
                myRef.orderByKey().startAt(s.toString().toLowerCase()).endAt(s.toString().toLowerCase()+"\uf8ff").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        String temp = snapshot.getKey().substring(0,1).toUpperCase()+snapshot.getKey().substring(1);
                        searchedClass.add(temp);
                        descriptions.add(snapshot.child("description").getValue().toString());
                        CustomClassListV2 adapter = new CustomClassListV2(GymClasses.this, searchedClass, descriptions);
                        myGymClasses.setAdapter(adapter);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                //scrollView.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(GymClasses.this.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(classSearch.getWindowToken(), 0);
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<String> nameList = new ArrayList<>();
                final ArrayList<String> desList = new ArrayList<>();
                final CustomClassListV2 adapter = new CustomClassListV2(GymClasses.this, nameList, desList);
                myGymClasses.setAdapter(adapter);

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String tempName = dataSnapshot.child("className").getValue().toString();
                    tempName = tempName.substring(0, 1).toUpperCase()+tempName.substring(1);
                    nameList.add(tempName);
                    desList.add(dataSnapshot.child("description").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}