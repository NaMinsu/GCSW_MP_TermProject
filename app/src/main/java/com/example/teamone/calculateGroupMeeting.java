package com.example.teamone;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class calculateGroupMeeting extends Activity {
    Button setting,calculating,cancel;
    TextView timeset;
    FirebaseDatabase Database  =FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = Database.getReference("schedule");
    DatabaseReference groupRef = Database.getReference("grouplist");
    DatabaseReference planRef = Database.getReference("plan");
    DatabaseReference personalRef = Database.getReference("users");
    ArrayList<String> members;
    ArrayList<Schedule> total;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_caculategroupmeeting);

        Intent intent = getIntent();
        String groupCode = intent.getStringExtra("code");
        String name = intent.getStringExtra("name");
        setting = findViewById(R.id.BtnTimesetting);
        timeset = findViewById(R.id.timeSetting);
        cancel = findViewById(R.id.calculateScheduleCancel);
        calculating = findViewById(R.id.calculateSchedule);
        members = new ArrayList<>();
        total = new ArrayList<>();


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(calculateGroupMeeting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            String hour, minute;

                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                if (i < 10) {
                                    hour = "0" + Integer.toString(i);
                                } else {
                                    hour = Integer.toString(i);
                                }
                                if (i1 < 10) {
                                    minute = "0" + Integer.toString(i1);
                                } else {
                                    minute = Integer.toString(i1);
                                }
                                timeset.setText(hour + ":" + minute);
                            }

                        }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        calculating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTime = timeset.getText().toString();
                boolean correct = true;

                if(!getTime.equals("시간 설정")){
                    String[] timesets = getTime.split(":");

                        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {


                                if(!task.getResult().getValue().toString().equals("0")){
                                    Toast.makeText(getApplicationContext(),"이미 그룹 미팅시간이 잡혀있습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }

                        });

                        Schedule[] baseSchedule = new Schedule[total.size()];
                        int i =0;
                        for(Schedule s:total) {
                            baseSchedule[i++] = s;
                        }
                        Schedule createTime = new Schedule();
                        int length = Integer.parseInt(timesets[0])*100 + Integer.parseInt(timesets[1]);


                        if (calculate(baseSchedule, createTime, length)) {
                            groupRef.child(groupCode).child("GroupSchedule").child("schedule").setValue(createTime.getDay() + "!/" + name + "'s meeting" + "!/" + createTime.getStartTime().getHour() + ":" + createTime.getStartTime().getMinute() + "!/" + createTime.getEndTime().getHour() + ":" + createTime.getEndTime().getMinute());
                            Toast.makeText(getApplicationContext(), "미팅이 추가되었습니다. 새로고침 버튼을 눌러 확인해주세요.", Toast.LENGTH_SHORT).show();

                            finish();
                        }

                    finish();
                }
                else
                    Toast.makeText(calculateGroupMeeting.this,"시간을 설정해주세요",Toast.LENGTH_SHORT).show();

            }
        });


        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot member : task.getResult().getChildren()){
                    members.add(member.getKey());
                    Toast.makeText(getApplicationContext(),member.getKey(),Toast.LENGTH_SHORT).show();

                    planRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for(DataSnapshot schedules : task.getResult().getChildren()){
                                String a = schedules.getKey();
                                planRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String date = task.getResult().child("date").getValue().toString();

                                        String[] dateSplit = date.split("/");
                                        LocalDate tmpDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
                                        int weekday = getWeekdayIndex(tmpDate.getDayOfWeek().toString());
                                        String[] times = Time.split("~");
                                        String[] startTime=times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        Schedule temp = new Schedule();
                                        temp.setDay(weekday);
                                        temp.setEndTime(new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                        temp.setStartTime(new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])));
                                        total.add(temp);
                                    }
                                });
                            }
                        }
                    });

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
                                        Schedule temp = new Schedule();
                                        temp.setDay(Integer.parseInt(weekday));
                                        temp.setEndTime(new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                        temp.setStartTime(new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])));
                                        total.add(temp);
                                    }
                                });//한사람의 스케쥴 한개 읽기
                            }
                        }
                    });//scheduleRef listener end

                }
            }
        });//groupRef listener end

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

                //요일별로 함수 실행, 그리고 여기서 available이 true가 나오면 true return하기
             if (Merging(day[i], merged[i], newSchedule, indicies[i], length,i)) {
                 return true;

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

    public int getWeekdayIndex(String weekday){
        if(weekday.equals("SUNDAY")){
            return 0;
        }else if(weekday.equals("MONDAY")){
            return 1;
        }else if(weekday.equals("TUESDAY")){
            return 2;
        }else if(weekday.equals("WEDNESDAY")){
            return 3;
        }else if(weekday.equals("THURSDAY")){
            return 4;
        }else if(weekday.equals("FRIDAY")){
            return 5;
        }else if(weekday.equals("SATURDAY")){
            return 6;
        }else{
            return 0;
        }
    }
}


