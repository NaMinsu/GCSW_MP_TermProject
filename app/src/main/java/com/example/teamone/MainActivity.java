package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

        addNew(1, "자료구조", "IT-402", "원킴", new Time(13, 00), new Time(16, 18));
        addNew(2, "자료구조", "IT-402", "원킴", new Time(17, 00), new Time(20, 30));
        addNew(4, "소프트웨어공학", "IT-603", "원킴", new Time(17, 00), new Time(21, 00));
        addNew(5, "중국어", "비전-302", "원킴", new Time(13, 00), new Time(15, 00));
        addNew(1, "모바일 프로그래밍", "IT-601", "원킴", new Time(17, 00), new Time(19, 00));
        addNew(4, "데이터과학", "IT-302", "원킴", new Time(9, 00), new Time(13, 00));

        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

        /*
        버튼 설정
         */

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

        String[] arrayTitle;
        String[] arrayContent;
        String[] arrayTime;

        /*
        numOfSchedule로 스케쥴의 개수를 읽은 뒤, 그 수만큼 반복문을 통해 배열에 스케쥴을 입력합니다.
        이후 배열을 리스트로 바꾸어 스케쥴에 올립니다.
        이 부분은 어떤 식으로 스케쥴 데이터를 읽어오느냐에 따라 달라질 수 있습니다.
         */
        int numOfSchedule = 5;
        arrayTitle = new String[numOfSchedule];
        arrayContent = new String[numOfSchedule];
        arrayTime = new String[numOfSchedule];

        for(int i=0;i<numOfSchedule;i++){
            arrayTitle[i] = "Title "+i;
            arrayContent[i] = "Content"+i;
            arrayTime[i] = "Time"+i;
        }

        List<String> listTitle = Arrays.asList(arrayTitle);
        List<String> listContent = Arrays.asList(arrayContent);
        List<String> listTime = Arrays.asList(arrayTime);

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
