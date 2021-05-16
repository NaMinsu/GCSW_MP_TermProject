package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.MainScheduleFragment;

public class MainActivity extends AppCompatActivity {
    MainScheduleFragment ScheduleFragment;
    GroupListFragment GroupListFragment;
    FriendListFragment FriendListFragment;
    SettingsFragment SettingsFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScheduleFragment = new MainScheduleFragment();
        GroupListFragment = new GroupListFragment();
        FriendListFragment = new FriendListFragment();
        SettingsFragment = new SettingsFragment();

        Intent intent = getIntent();
        String s = intent.getStringExtra("fragment");

        if(s.equals("1")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupListFragment).commit();
        }
        else if(s.equals("2")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment).commit();
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
                ft.replace(R.id.fragment_container,ScheduleFragment).commit();
            }
        });

        Button groupB = (Button) selfLayout.findViewById(R.id.btnGroup);
        groupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,GroupListFragment).commit();
            }
        });

        Button friendB = (Button) selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,FriendListFragment).commit();
            }
        });

        Button settingB = (Button) selfLayout.findViewById(R.id.btnSetUp);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,SettingsFragment).commit();
            }
        });


    }
}