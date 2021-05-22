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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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

        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if(!task.getResult().getValue().toString().equals("0")){
                    String s = task.getResult().getValue().toString();
                    String[] fortable = s.split("!/");
                    String[] starttime = fortable[2].split(":");
                    String[] Endtime = fortable[3].split(":");
                    addNew(Integer.parseInt(fortable[0]),fortable[1],"",new Time(Integer.parseInt(starttime[0]),Integer.parseInt(starttime[1])),new Time(Integer.parseInt(Endtime[0]),Integer.parseInt(Endtime[1])));
                }

            }
        });

        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot member : task.getResult().getChildren()){
                    members.add(member.getKey());
                    scheduleRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for(DataSnapshot schedules : task.getResult().getChildren()){
                                String a = schedules.getKey();
                                scheduleRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String weekday = task.getResult().child("weekday").getValue().toString();
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String[] times = Time.split("~");
                                        String[] startTime=times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        addNew(Integer.parseInt(weekday),"","",new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])),new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                    }
                                });//한사람의 스케쥴 한개 읽기
                            }
                        }
                    }   );
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


        Button resetB = (Button)findViewById(R.id.reset);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        Button calculating = (Button)findViewById(R.id.calculate);
        calculating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),calculateGroupMeeting.class);
                intent.putExtra("name",name);
                intent.putExtra("code",groupCode);
                startActivity(intent);
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
                finish();
            }
        });

    }

    public void reset(){
        members = new ArrayList<>();
        timetable.removeAll();

        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if(!task.getResult().getValue().toString().equals("0")){
                    String s = task.getResult().getValue().toString();
                    String[] fortable = s.split("!/");
                    String[] starttime = fortable[2].split(":");
                    String[] Endtime = fortable[3].split(":");
                    addNew(Integer.parseInt(fortable[0]),fortable[1],"",new Time(Integer.parseInt(starttime[0]),Integer.parseInt(starttime[1])),new Time(Integer.parseInt(Endtime[0]),Integer.parseInt(Endtime[1])));
                }

            }
        });

        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot member : task.getResult().getChildren()){
                    members.add(member.getKey());

                    scheduleRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for(DataSnapshot schedules : task.getResult().getChildren()){
                                String a = schedules.getKey();
                                scheduleRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String weekday = task.getResult().child("weekday").getValue().toString();
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String[] times = Time.split("~");
                                        String[] startTime=times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        addNew(Integer.parseInt(weekday),"","",new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])),new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                    }
                                });//한사람의 스케쥴 한개 읽기
                            }
                        }
                    }   );
                }
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

    public boolean calculate(Schedule[] groupSchedule, Schedule newSchedule, int length) {

        int index = groupSchedule.length;

        Schedule[][] day = new Schedule[7][groupSchedule.length];

        int[] indicies = new int[7];

        for (int j = 0; j < 7; j++){
            for (int i = 0; i < index; i++) {
                day[j][i] = new Schedule();
            }
        }

        //요일 나누기
        for (int i = 0; i < index; i++) {
            if (groupSchedule[i].getDay() == 0) {
                day[0][indicies[0]] = groupSchedule[i];
                indicies[0]++;

            } else if (groupSchedule[i].getDay() == 1) {
                day[1][indicies[1]] = groupSchedule[i];
                indicies[1]++;
            } else if (groupSchedule[i].getDay() == 2) {
                day[2][indicies[2]] = groupSchedule[i];
                indicies[2]++;
            } else if (groupSchedule[i].getDay() == 3) {
                day[3][indicies[3]] = groupSchedule[i];
                indicies[3]++;
            } else if (groupSchedule[i].getDay() == 4) {
                day[4][indicies[4]] = groupSchedule[i];
                indicies[4]++;
            } else if (groupSchedule[i].getDay() == 5) {
                day[5][indicies[5]] = groupSchedule[i];
                indicies[5]++;
            } else if (groupSchedule[i].getDay() == 6) {
                day[6][indicies[6]] = groupSchedule[i];
                indicies[6]++;
            }
        }
        //나눈 요일별로 merging function의 결과를 담을 class
        Schedule[][] merged = new Schedule[7][index];

        //전체 초기화 및 요일별로 함수 돌리기
        for (int i = 0; i < 7; i++) {

            for(int j=0;j<index;j++){//요일 전체 초기화
                merged[i][j] = new Schedule();
            }
            if (indicies[i] != 0) {
                System.out.println(i);
                //요일별로 함수 실행, 그리고 여기서 available이 true가 나오면 true return하기
                if (Merging(day[i], merged[i], newSchedule, indicies[i], length,i)) {
                    return true;
                }
            }
        }
        return false; //모든요일에서 false가 return되면 return false
    }


    public boolean Merging(Schedule[] days,Schedule[] merging,Schedule newschedule, int index,int length,int day) {

        Schedule temp = new Schedule();
        int done=0;

        int minIndex= 0;
        int minStartTime=2400; //시간이기 때문에 24가 가장 큰 수 & 시간계산은 hour * 100 + minute로 할것.

        for(int i = 0;i<index-1;i++){
            minIndex = i;
            for(int j = i; j < index; j++){
                if(days[j].getStartTime().getHour()*100 + days[i].getStartTime().getMinute() < days[minIndex].getStartTime().getHour()*100 + days[i].getStartTime().getMinute()) {
                    minIndex = j;
                }

                temp = days[minIndex];
                days[minIndex] = days[i];
                days[i] = temp;

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

        return available(merging,newschedule,count,length,day);

    }//merging function end





    //합친 것을 바탕으로 boolean함수로 가능한지 확인하기
    //length 받은 것을 바탕으로 merge완료 된 각 날짜의 스케줄의 끝나는 시간과 시작시간을 빼면서 length보다 작은 값 나오는 것들 다 찾기.
    //만약 여기서 true가 나온다면 전체 함수 끝
    public boolean available(Schedule[] days, Schedule sample, int index,int length,int day) {

        sample.setDay(day);

        if (index == 1) { // 한개로 전체가 merging된 경우 시작시간 - 9시 or 10시 - 끝난시간 해서 이게 length 안이면 return true

            if (days[0].getStartTime().getHour() * 100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9, 0));
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            } else if (2200 - days[0].getEndTime().getHour() * 100 + days[0].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[0].getEndTime());
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            } else return false;
        } else if (index > 1) {   // index가 2개 이상이면 index 0 의 시작시간 - 9시 해서 length 안쪽이면 return true 아닐 경우  (index i+1 시작 시간) - (index i 끝시간) 일 경우 return true, 아니면 10시(default 마지막 시간) - (index i 끝시간) 이면 return true


            if (days[0].getStartTime().getHour() * 100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9, 0));
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            }

            for (int i = 0; i < index - 1; i++) {


                int hour = days[i + 1].getStartTime().getHour() - days[i].getEndTime().getHour();
                int minute = days[i + 1].getStartTime().getMinute() - days[i].getEndTime().getMinute();

                if (minute < 0) {
                    hour--;
                    minute = 60 + minute;
                }//60분이기 때문에 그에 맞춰서 다시 계산해주기


                if (hour * 100 + minute >= length) {

                    sample.setStartTime(new Time(days[i].getEndTime().getHour(), days[i].getEndTime().getMinute()));

                    int endMinute = days[i].getEndTime().getMinute() + length % 100;
                    int endHour = days[i].getEndTime().getHour() + length / 100;
                    if (endMinute >= 60) {
                        endHour++;
                        endMinute = endMinute - 60;
                    }

                    sample.setEndTime(new Time(endHour, endMinute));

                    return true;

                }
            }

            if (2200 - days[index - 1].getEndTime().getHour() * 100 + days[index - 1].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[index - 1].getEndTime());
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            }

        }
        return false;
    }
}
