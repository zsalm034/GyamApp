package com.example.testgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAccountPage extends AppCompatActivity {

    private ListView instructorsList;
    private ListView membersList;
    private DatabaseReference myRef;
    private FirebaseUser myUser;
    private final String[] selectedUser = {""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_page);

        instructorsList = findViewById(R.id.listinst);
        membersList = findViewById(R.id.memList);
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        final boolean[] isInstructor = {false};

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<String> members = new ArrayList<>();
                final ArrayList<String> instructors = new ArrayList<>();
                final ArrayAdapter membersAdapter = new ArrayAdapter<String>(UserAccountPage.this, R.layout.account_item, members);
                final ArrayAdapter instructorAdapter = new ArrayAdapter<String>(UserAccountPage.this, R.layout.account_item, instructors);
                membersList.setAdapter(membersAdapter);
                instructorsList.setAdapter(instructorAdapter);

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (!dataSnapshot.child("role").getValue().toString().equals("Administrator")){
                        if (dataSnapshot.child("role").getValue().toString().equals("Member")){

                            members.add(dataSnapshot.getKey().toString()+" - "+dataSnapshot.child("name").getValue().toString());
                        }
                        else {
                            instructors.add(dataSnapshot.getKey().toString()+" - "+dataSnapshot.child("name").getValue().toString());
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(UserAccountPage.this);
        builder.setMessage("Are you sure you want to delete this user ?")
                .setTitle("Delete user").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ArrayList<String> userClasses = new ArrayList<>();
                        ArrayList<String> userClassType = new ArrayList<>();

                        if (!isInstructor[0]){
                            DatabaseReference classRef = myRef.child(selectedUser[0]).child("gymClasses").getRef();

                            classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            userClasses.add(dataSnapshot.getKey());
                                            userClassType.add(dataSnapshot.child("classType").getValue().toString());
                                        }

                                        for (int i = 0; i < userClasses.size(); i++) {
                                            final boolean[] clicked = {true};
                                            removeFromClass(clicked, userClassType.get(i), userClasses.get(i));
                                        }
                                        myRef.child(selectedUser[0]).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            myRef.child(selectedUser[0]).removeValue();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();

        membersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser[0] = adapterView.getItemAtPosition(i).toString().split("-")[0].trim();
                dialog.show();
            }
        });


        instructorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser[0] = adapterView.getItemAtPosition(i).toString().split("-")[0].trim();
                isInstructor[0] = true;
                dialog.show();
            }
        });

    }
    public void removeFromClass(boolean[] clicked, String selectedType, String selectedClassID){
        if (clicked[0]) {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("gymClass").child(selectedType).child(selectedClassID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (clicked[0]) {
                            long currentUsers = Integer.parseInt(snapshot.child("numberOfUsers").getValue().toString());
                            snapshot.child("numberOfUsers").getRef().setValue(currentUsers - 1);
                            String instructorID = snapshot.child("instructor").child("userID").getValue().toString();
                            DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(instructorID).getRef();
                            DatabaseReference tempRef2 = FirebaseDatabase.getInstance().getReference().child("gymClass").child(selectedType)
                                    .child(selectedClassID).child("members").child(selectedUser[0]).getRef();
                            tempRef2.removeValue();

                            tempRef.child("gymClasses").child(selectedClassID).child("numberOfUsers").setValue(currentUsers-1);
                            tempRef.child("gymClasses").child(selectedClassID).child("members").child(selectedUser[0]).getRef().removeValue();
                            clicked[0] = false;
                            finish();
                            startActivity(getIntent());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
}