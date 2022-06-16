package com.example.testgymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class RegirsteredUsers extends AppCompatActivity {
    private ListView regUsers;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regirstered_users);

        backButton = findViewById(R.id.backIcon);

        regUsers = findViewById(R.id.regUsersList);

        final ArrayAdapter membersAdapter = new ArrayAdapter<String>(RegirsteredUsers.this, R.layout.account_item, InstructorPage.usersList);
        regUsers.setAdapter(membersAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}