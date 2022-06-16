package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    private static final String TAG = "MyActivity";
    private EditText email;
    private EditText password;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mdb;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        mdb = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser!=null){
            loginUser(mUser);
        }
        else {

            setContentView(R.layout.activity_main);

            email = (EditText) findViewById(R.id.emailText);
            password = (EditText) findViewById(R.id.loginPassword);
            loginButton = (Button) findViewById(R.id.loginButton);

            TextView welcomeMessage = (TextView) findViewById(R.id.textView4);
            TextView createAccountLabel = (TextView) findViewById(R.id.createAccountText);

            email.setTextColor(Color.parseColor("#FFFFFF"));
            password.setTextColor(Color.parseColor("#FFFFFF"));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    welcomeMessage.setVisibility(View.INVISIBLE);
                }
            }, SPLASH_TIME_OUT);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean usernameEntered = verifyUsername();
                    boolean passwordVerified = verifyPassword();

                    if (usernameEntered && passwordVerified) {
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(
                                MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser mUser = mAuth.getCurrentUser();
                                            if (mUser != null) {
                                                loginUser(mUser);
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Username/Password invalid", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });

            createAccountLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCreate();
                }
            });
        }
    }

    public void onClickCreate (){
        Intent createAccIntent = new Intent(this, CreateAccount.class);
        startActivity(createAccIntent);
    }
    public boolean verifyUsername(){
        EditText email = (EditText) findViewById(R.id.emailText);

        if (TextUtils.isEmpty(email.getText())) {
            email.setError("Username is required!");
            return false;
        }
        return true;
    }
    public boolean verifyPassword(){
        EditText password = (EditText) findViewById(R.id.loginPassword);

        if(TextUtils.isEmpty(password.getText())){
            password.setError("Password is required !");
            return false;
        }
        return true;
    }
    private void loginUser(FirebaseUser myUser){

        String uid = myUser.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user =  snapshot.getValue(User.class);
                    assert user != null;

                    String role;
                    String tempName;

                    role = user.getRole();
                    tempName = user.getName();
                    if (role.equals("Member")){
                        launchMemberPage(tempName, role);
                    }
                    else if (role.equals("Instructor")){
                        launchInstructorPage(tempName, role);
                    }
                    else {
                        launchAdminPage(tempName, role);
                    }
                }else {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "This account does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void launchAdminPage(String name, String role){
        Intent welcomeIntent = new Intent(MainActivity.this, AdminPage.class);
        welcomeIntent.putExtra("name", name);
        welcomeIntent.putExtra("role", role);
        startActivity(welcomeIntent);
        finish();
    }
    private void launchInstructorPage(String name, String role){
        Intent welcomeIntent = new Intent(MainActivity.this, InstructorPage.class);
        welcomeIntent.putExtra("name", name);
        welcomeIntent.putExtra("role", role);
        startActivity(welcomeIntent);
        finish();
    }
    private void launchMemberPage(String name, String role){
        Intent welcomeIntent = new Intent(MainActivity.this, GymMemberPage.class);
        welcomeIntent.putExtra("name", name);
        welcomeIntent.putExtra("role", role);
        startActivity(welcomeIntent);
        finish();
    }
}