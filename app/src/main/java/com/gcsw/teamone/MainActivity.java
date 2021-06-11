package com.gcsw.teamone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    FragmentSchedule ScheduleFragment;
    FragmentGroupList FragmentGroupList;
    FragmentFriendList FragmentFriendList;
    FragmentSettings FragmentSettings;
    MediaPlayer mediaPlayer;
    /*We've linked each fragment to the main activity.*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScheduleFragment = new FragmentSchedule();
        FragmentGroupList = new FragmentGroupList();
        FragmentFriendList = new FragmentFriendList();
        FragmentSettings = new FragmentSettings();
        mediaPlayer = MediaPlayer.create(this, R.raw.thik);

        Intent intent = getIntent();
        String s = intent.getStringExtra("fragment");

        if(s.equals("1")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FragmentGroupList).commit();
        } //start main activity with fragmentGroupList when touch 뒤로가기 in group table
        else if(s.equals("2")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FragmentSettings).commit();
        } //start main activity with fragment setting when touch 뒤로가기 in change info
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ScheduleFragment).commit();
        }

        LinearLayout selfLayout = (LinearLayout) findViewById(R.id.mainLayout);


        ImageButton myPageB = (ImageButton) findViewById(R.id.btnMyPage);
        myPageB.setSoundEffectsEnabled(false);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,ScheduleFragment);
                ft.addToBackStack(null).commit();
            }
        });

        ImageButton groupB = (ImageButton) selfLayout.findViewById(R.id.btnGroup);
        groupB.setSoundEffectsEnabled(false);
        groupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentGroupList);
                ft.addToBackStack(null).commit();
            }
        });

        ImageButton friendB = (ImageButton) selfLayout.findViewById(R.id.btnFriend);
        friendB.setSoundEffectsEnabled(false);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentFriendList);
                ft.addToBackStack(null).commit();
            }
        });

        ImageButton settingB = (ImageButton) selfLayout.findViewById(R.id.btnSetUp);
        settingB.setSoundEffectsEnabled(false);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentSettings);
                ft.addToBackStack(null).commit();
            }
        });

    }
    private void killMediaPlayer(){
        if(mediaPlayer!=null){
            try{mediaPlayer.release();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        killMediaPlayer();
    }
}