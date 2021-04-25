package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TimetableView timetable;
    private todaySchedule adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout selfLayout = (LinearLayout)findViewById(R.id.mainLayout);

        init();
        getData();

        timetable = (TimetableView)findViewById(R.id.timetable);

        addNew(1,"자료구조","우리집", "원킴", new Time(13,00),new Time(16,18));
        addNew(2, "자료구조","우리집", "원킴", new Time(17,00),new Time(20,30));
        addNew(4, "자료구조","우리집", "원킴", new Time(17,00),new Time(21,00));


        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);

        Button groupB = (Button)selfLayout.findViewById(R.id.btnGroup);
        groupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupList.class);
                startActivity(intent);
            }
        });

        Button friendB = (Button)selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), friendList.class);
                startActivity(intent);
            }
        });

        Button settingB = (Button)selfLayout.findViewById(R.id.btnSetUp);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), settings.class);
                startActivity(intent);
            }
        });

    }

    protected void addNew(int day, String title, String place, String prof, Time startTime, Time endTime){
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        Schedule schedule = new Schedule();
        schedule.setClassTitle(title); // sets subject
        schedule.setClassPlace(place); // sets place
        schedule.setProfessorName(prof); // sets professor
        schedule.setStartTime(startTime); // sets the beginning of class time (hour,minute)
        schedule.setEndTime(endTime); // sets the end of class time (hour,minute)
        schedule.setDay(day);
        schedules.add(schedule);

        timetable.add(schedules);
    }

    private void init(){
        RecyclerView recyclerView = findViewById(R.id.scheduleRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new todaySchedule();
        recyclerView.setAdapter(adapter);
    }

    private void getData(){
        List<String> listTitle = Arrays.asList("철수 만나기", "영희 만나기", "혼자 놀기","다 같이 놀기");
        List<String> listContent = Arrays.asList("재밌게 놀기", "신나게 놀기", "심심하게 놀기", "북적북적");
        List<String> listTime = Arrays.asList("12:30 ~ 13:30", "14:40~15:40","17:00~18:00","19:00~21:00");

        for(int i=0; i<listTitle.size();i++){
            todayScheduleData data = new todayScheduleData();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setTime(listTime.get(i));

            adapter.addItem(data);
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}