package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TimetableView timetable;
    private todaySchedule adapter;
    int numOfPlan = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout selfLayout = (LinearLayout)findViewById(R.id.mainLayout);

        init();

        timetable = (TimetableView)findViewById(R.id.timetable);
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

        /*
        버튼 설정
         */

        Button addSchedule = findViewById(R.id.addScheduleBtn);

        addSchedule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addSchedule.class);
                startActivityForResult(intent,0);
            }
        });

        Button addPlan = findViewById(R.id.addPlanBtn);

        addPlan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addPlan.class);
                startActivityForResult(intent,1);
            }
        });

        Button myPageB = (Button)findViewById(R.id.btnMyPage);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==0){
            if(resultCode==RESULT_OK){
                String scheduleName = data.getStringExtra("scheduleName");
                String startHour = data.getStringExtra("startHour");
                String startMinute = data.getStringExtra("startMinute");
                String endHour = data.getStringExtra("endHour");
                String endMinute = data.getStringExtra("endMinute");
                int weekdayIndex = data.getIntExtra("weekdayIndex",1);
                Time start = new Time(Integer.parseInt(startHour),Integer.parseInt(startMinute));
                Time end = new Time(Integer.parseInt(endHour),Integer.parseInt(endMinute));

                addNew(weekdayIndex,scheduleName,"",start,end);
            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }else if(requestCode==1){
            if(resultCode==RESULT_OK){
                String scheduleName = data.getStringExtra("planName");
                String startHour = data.getStringExtra("startHour");
                String startMinute = data.getStringExtra("startMinute");
                String endHour = data.getStringExtra("endHour");
                String endMinute = data.getStringExtra("endMinute");
                int weekdayIndex = data.getIntExtra("weekdayIndex",1);
                Time start = new Time(Integer.parseInt(startHour),Integer.parseInt(startMinute));
                Time end = new Time(Integer.parseInt(endHour),Integer.parseInt(endMinute));

                getData(scheduleName,scheduleName,startHour+":"+startMinute+"~"+endHour+":"+endMinute);
            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

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

    private void init(){
        RecyclerView recyclerView = findViewById(R.id.scheduleRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new todaySchedule();
        recyclerView.setAdapter(adapter);
    }

    private void getData(String title, String content, String time){

        numOfPlan++;

        todayScheduleData data = new todayScheduleData();
        /*
        numOfSchedule로 스케쥴의 개수를 읽은 뒤, 그 수만큼 반복문을 통해 배열에 스케쥴을 입력합니다.
        이후 배열을 리스트로 바꾸어 스케쥴에 올립니다.
        이 부분은 어떤 식으로 스케쥴 데이터를 읽어오느냐에 따라 달라질 수 있습니다.
         */

        data.setTitle(title);
        data.setContent(content);
        data.setTime(time);
        adapter.addItem(data);

        adapter.notifyDataSetChanged();

    }
    private void addData(){

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
