package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collection;
import java.util.Collections;
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
    ArrayList<Schedule> total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouptable);
        View selfLayout = (View) findViewById(R.id.gtLayout);

        members = new ArrayList<>();
        total = new ArrayList<>();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        groupCode = intent.getStringExtra("code");

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




        Button loading = (Button)findViewById(R.id.loadTable);
        loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(String s:members) {
                    scheduleRef.child(s).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for(DataSnapshot schedules : task.getResult().getChildren()){
                                String a = schedules.getKey();
                                scheduleRef.child(s).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String weekday = task.getResult().child("weekday").getValue().toString();
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String[] times = Time.split("~");
                                        String[] startTime=times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        Schedule loaded = new Schedule();
                                        loaded.setDay(Integer.parseInt(weekday));
                                        loaded.setStartTime(new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])));
                                        loaded.setEndTime(new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                        total.add(loaded);
                                        }
                                });//한사람의 스케쥴 한개 읽기
                            }
                        }
                    });//한 사람씩 스케쥴읽기 전체 반복
                }

            }
        });


        Button calculating = (Button)findViewById(R.id.calculate);
        calculating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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


    public void merge(ArrayList<Schedule> totals){

        ArrayList<Schedule>[] forMerge = new ArrayList[7];

        for(Schedule s:totals){
            switch(s.getDay()){
                case 0:
                    forMerge[0].add(s);
                    break;

                case 1:
                    forMerge[1].add(s);
                    break;

                case 2:
                    forMerge[2].add(s);
                    break;

                case 3:
                    forMerge[3].add(s);
                    break;

                case 4:
                    forMerge[4].add(s);
                    break;

                case 5:
                    forMerge[5].add(s);
                    break;

                case 6:
                    forMerge[6].add(s);
                    break;
            }

        }//요일별로 나누기


        ArrayList<Schedule>[] Merged = new ArrayList[7];


    }

    public boolean Merging(ArrayList<Schedule> days,ArrayList<Schedule> merging) {

        Schedule temp = new Schedule();
        int done=0;

        int minIndex= 0;
        int minStartTime=2400; //시간이기 때문에 24가 가장 큰 수 & 시간계산은 hour * 100 + minute로 할것.


        int index = days.size();
        for(int i = 0;i<index-1;i++){
            minIndex = i;
            for(int j = i; j < index; j++){
                if(days.get(i).getStartTime().getHour()*100 + days.get(i).getStartTime().getMinute() < days.get(minIndex).getStartTime().getHour()*100 + days.get(minIndex).getStartTime().getMinute()) {
                    minIndex = j;
                }

                Collections.swap(days,i,minIndex);

            }
        } //sorting


        // need to merging them  -> We don't need to show name and title in this table. So only use time
        int count =0; //merging 의 갯수 새기
        // done는 merge에 포함된 array의 갯수
        // done = 0부터 시작,

        while(done != index) {
            merging[count].setStartTime(days[done].getStartTime());
            merging[count].setEndTime(days[done].getEndTime());


            for (int i = done; i < index; i++) {
                if (merging[count].getStartTime().getHour() * 100 + merging[count].getStartTime().getMinute() <= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()// merging의 시작시간이 더 빠르고
                        && merging[count].getEndTime().getHour()*100 + merging[count].getEndTime().getMinute() >= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()  // merging의 시작과 끝 사이에 새로운 스케쥴의 시작시간이 있고
                        &&i != done
                ) {

                    if(merging[count].getEndTime().getHour()*100 + merging[count].getEndTime().getMinute() <days[i].getEndTime().getHour() * 100 + days[i].getEndTime().getMinute()) {//merging의 끝시간 보다 새로운 스케쥴의 끝이 더 느릴때
                        merging[count].setEndTime(days[i].getEndTime()); //더 늦게 끝나는걸 merging의 끝나는 시간으로 선택하기
                        done++;//몇개가 합쳐졌는지 카운트
                        if (done == index)
                            break;
                    }

                    else {
                        done++;
                        if (done == index)
                            break;
                    }
                }



            }
            if (done == index)
                break;
            done++;
            count++; //한바퀴 다 돌고 아직 모든 array의 value들이 안합쳐졌다면 나머지도 합쳐야함.
        }

        return available(merging,newschedule,count,length);

    }//merging function end

}
