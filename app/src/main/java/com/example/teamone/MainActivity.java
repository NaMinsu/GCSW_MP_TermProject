package com.example.teamone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    FragmentSchedule ScheduleFragment;
    FragmentGroupList FragmentGroupList;
    FragmentFriendList FragmentFriendList;
    FragmentSettings FragmentSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScheduleFragment = new FragmentSchedule();
        FragmentGroupList = new FragmentGroupList();
        FragmentFriendList = new FragmentFriendList();
        FragmentSettings = new FragmentSettings();

        Intent intent = getIntent();
        String s = intent.getStringExtra("fragment");

        if(s.equals("1")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FragmentGroupList).commit();
        }
        else if(s.equals("2")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FragmentSettings).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ScheduleFragment).commit();
        }

        LinearLayout selfLayout = (LinearLayout) findViewById(R.id.mainLayout);


        Button myPageB = (Button) findViewById(R.id.btnMyPage);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,ScheduleFragment);
                ft.addToBackStack(null).commit();
            }
        });

        Button groupB = (Button) selfLayout.findViewById(R.id.btnGroup);
        groupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentGroupList);
                ft.addToBackStack(null).commit();
            }
        });

        Button friendB = (Button) selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentFriendList);
                ft.addToBackStack(null).commit();
            }
        });

        Button settingB = (Button) selfLayout.findViewById(R.id.btnSetUp);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentSettings);
                ft.addToBackStack(null).commit();
            }
        });

    }

}