package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class groupTable extends AppCompatActivity {

    TimetableView timetable;
    private todaySchedule adapter;
    FirebaseDatabase Database  =FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = Database.getReference("schedule");
    DatabaseReference groupRef = Database.getReference("grouplist");
    ArrayList<String> members; /*이 리스트는 나중에 푸시 알림을 보낼때*/
    String name;               /*선택한 맴버들의 토큰 정보를 저장하는곳으로 ?*/
    String groupCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouptable);
        View selfLayout = (View) findViewById(R.id.gtLayout);

        members = new ArrayList<>();


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        groupCode = intent.getStringExtra("code");
        Toast.makeText(getApplicationContext(),groupCode,Toast.LENGTH_SHORT).show();

        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot member : task.getResult().getChildren()){
                    members.add(member.getKey());
                }
            }
        });

        timetable = (TimetableView)findViewById(R.id.timetable_group);


        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

        Button AddMember = (Button)selfLayout.findViewById(R.id.btnAddMember);
        AddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupMemberAdder.class);
                intent.putExtra("name",name);
                intent.putExtra("code",groupCode);
                startActivity(intent);
            }
        });


        Button goBack = (Button)selfLayout.findViewById(R.id.btnToGList);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment","1");
                startActivity(intent);
            }
        });

    }

    protected void addNew(int day, String title, String place, Time startTime, Time endTime){
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        Schedule schedule = new Schedule();
        schedule.setClassTitle(title); // sets subject
        schedule.setClassPlace(place); // sets place
        schedule.setStartTime(startTime); // sets the beginning of class time (hour,minute)
        schedule.setEndTime(endTime); // sets the end of class time (hour,minute)
        schedule.setDay(day);
        schedules.add(schedule);

        timetable.add(schedules);
    }


}
