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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminPage extends AppCompatActivity {
    private TextView welcomeText;
    private Button createClass;
    private LinearLayout mainContent;
    private LinearLayout createClassOverlay;
    private EditText className;
    private EditText classDescription;
    private Button endClassCreate;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser ;
    private DatabaseReference myRef;
    private ListView profileOptions;
    private NavigationView profileOptionsLayout;
    private NavigationView sideNav;
    private ListView menuOptions;
    private ImageView showMenu;
    private ImageView showProfile;


    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        createClass = (Button) findViewById(R.id.createClassButton);
        createClassOverlay = (LinearLayout) findViewById(R.id.createClassOverlay);
        mainContent = (LinearLayout) findViewById(R.id.mainContent);
        welcomeText = findViewById(R.id.welcomeMessageAdmin);
        endClassCreate = findViewById(R.id.completeClass);
        className = findViewById(R.id.classNameField);
        classDescription = findViewById(R.id.classDescriptionField);
        showProfile = findViewById(R.id.topRightIcon);
        showMenu= findViewById(R.id.topLeftIcon);
        menuOptions = findViewById(R.id.navMenuOptions);
        sideNav = findViewById(R.id.sideNav);
        profileOptionsLayout = findViewById(R.id.profileOptionsMenu);
        profileOptions = findViewById(R.id.profileOptionsList);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

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
        options.add("Available Classes");
        options.add("User Accounts");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminPage.this, R.layout.nav_menu_options, options);
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
                else if (position==1){
                    Intent classesIntent = new Intent(getApplicationContext(), UserAccountPage.class);
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
                crossFade(sideNav, state);
                profileOptionsLayout.setVisibility(View.GONE);
            }
        });

        //Profile Menu Options
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> profileMenuAdapter = new ArrayAdapter<>(AdminPage.this, R.layout.nav_menu_options, arrayList);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPage.this);
        builder.setMessage("Are you sure you want to sign out ?")
                .setTitle("Sign out").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        Intent loginIntent = new Intent(AdminPage.this, MainActivity.class);
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

        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crossFade(createClassOverlay, View.VISIBLE);
                showMenu.setClickable(false);
                showProfile.setClickable(false);
            }
        });

        endClassCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser!=null){
                    String name = className.getText().toString().trim().toLowerCase();
                    String description = classDescription.getText().toString().trim();
                    GymClassType aGymClass= new GymClassType(name, description);
                    if (name.length()!=0 && description.length()!=0) {
                        addGymClass(aGymClass);
                        myRef.child("gymClassType").child(name).getRef().setValue(aGymClass);
                        createClassOverlay.setVisibility(View.GONE);
                        className.setText("");
                        classDescription.setText("");
                        mainContent.setClickable(true);
                        createClassOverlay.setClickable(false);
                    }
                    else {
                        Toast.makeText(AdminPage.this, "Class name/Description should not be empty", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    public void addGymClass(GymClassType gymClass){
        final String[] name = {gymClass.getClassName()};
        final boolean[] success = {false};

        myRef.child("gymClasses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(name[0]).exists() && className.getText().toString().length()!=0){
                    Toast.makeText(AdminPage.this, "Class already exist", Toast.LENGTH_SHORT).show();
                }
                else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //preventing default implementation previous to
            //android.os.Build.VERSION_CODES.ECLAIR
            if (createClassOverlay.getVisibility()==View.VISIBLE){
                crossFade(createClassOverlay, View.GONE);
                showProfile.setClickable(true);
                showMenu.setClickable(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}