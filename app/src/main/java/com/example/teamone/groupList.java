package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

public class groupList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);
        View selfLayout = (View)findViewById(R.id.glLayout);

        ScrollView groups = (ScrollView)selfLayout.findViewById(R.id.glist);

        Button groupAddB = (Button)selfLayout.findViewById(R.id.btnAddGroup);
        groupAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button groupB = (Button)selfLayout.findViewById(R.id.btnGroup);

        Button friendB = (Button)selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button settingB = (Button)selfLayout.findViewById(R.id.btnSetUp);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}