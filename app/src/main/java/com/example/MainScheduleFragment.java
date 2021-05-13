package com.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamone.FirstAuthActivity;
import com.example.teamone.R;
import com.example.teamone.Schedule;
import com.example.teamone.Time;
import com.example.teamone.TimetableView;
import com.example.teamone.addPlan;
import com.example.teamone.addSchedule;
import com.example.teamone.todaySchedule;
import com.example.teamone.todayScheduleData;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainScheduleFragment extends Fragment {

    TimetableView timetable;
    private todaySchedule adapter;
    int numOfPlan = 0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference planRef = database.getReference("plan");
    DatabaseReference scheduleRef = database.getReference("schedule");
    ArrayList<todayScheduleData> plans = new ArrayList<todayScheduleData>();

    String title,date,time,startDate,endDate;
    int startHour,startMinute,endHour,endMinute;
    int weekday;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_schedules,container,false);

        LinearLayout selfLayout = v.findViewById(R.id.mainLayout);

        init(v);

        timetable = v.findViewById(R.id.timetable);
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

        /*
        버튼 설정
         */

        Button addSchedule = v.findViewById(R.id.addScheduleBtn);

        addSchedule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.teamone.addSchedule.class);
                startActivity(intent);
            }
        });

        Button addPlan = v.findViewById(R.id.addPlanBtn);

        addPlan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.teamone.addPlan.class);
                startActivity(intent);
            }
        });

        return v;
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

    private void init(View v){
        RecyclerView recyclerView = v.findViewById(R.id.scheduleRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume(){
        super.onResume();
        adapter.remove();
        timetable.removeAll();

        LocalDate nowDate = LocalDate.now();
        LocalDate sunday = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate satday = nowDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        planRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                for (DataSnapshot plan : task.getResult().getChildren()) {
                    title = plan.child("title").getValue().toString();
                    date = plan.child("date").getValue().toString();
                    time = plan.child("time").getValue().toString();
                    String[] timeSplit = time.split("~");
                    startHour = Integer.parseInt(timeSplit[0].substring(0,2));
                    startMinute = Integer.parseInt(timeSplit[0].substring(3,5));
                    endHour = Integer.parseInt(timeSplit[1].substring(0,2));
                    endMinute = Integer.parseInt(timeSplit[1].substring(3,5));

                    String[] dateSplit = date.split("/");
                    LocalDate tmpDate = LocalDate.of(Integer.parseInt(dateSplit[0]),Integer.parseInt(dateSplit[1]),Integer.parseInt(dateSplit[2]));
                    weekday = getWeekdayIndex(tmpDate.getDayOfWeek().toString());

                    if(getDateDif(tmpDate,sunday)<0) {
                        deletePlan(title,dateSplit[0]+dateSplit[1]+dateSplit[2],time);
                    }else if(getDateDif(tmpDate,sunday)>0&&getDateDif(tmpDate,satday)<0){
                        addNew(weekday,title,"",new Time(startHour,startMinute),new Time(endHour,endMinute));
                        getData(title, date,time);
                    }else{
                        getData(title, date,time);
                    }
                }
            }

        });

        scheduleRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                for (DataSnapshot schedule : task.getResult().getChildren()) {

                    title = schedule.child("title").getValue().toString();
                    startDate = schedule.child("startDate").getValue().toString();
                    endDate = schedule.child("endDate").getValue().toString();
                    time = schedule.child("time").getValue().toString();
                    weekday = Integer.parseInt(schedule.child("weekday").getValue().toString());
                    String[] timeSplit = time.split("~");
                    startHour = Integer.parseInt(timeSplit[0].substring(0,2));
                    startMinute = Integer.parseInt(timeSplit[0].substring(3,5));
                    endHour = Integer.parseInt(timeSplit[1].substring(0,2));
                    endMinute = Integer.parseInt(timeSplit[1].substring(3,5));

                    String[] startDateSplit = startDate.split("/");
                    String[] endDateSplit = endDate.split("/");
                    LocalDate startLocalDate = LocalDate.of(Integer.parseInt(startDateSplit[0]),Integer.parseInt(startDateSplit[1]),Integer.parseInt(startDateSplit[2]));
                    LocalDate endLocalDate = LocalDate.of(Integer.parseInt(endDateSplit[0]),Integer.parseInt(endDateSplit[1]),Integer.parseInt(endDateSplit[2]));

                    if(getDateDif(nowDate,startLocalDate)>=0&&getDateDif(nowDate,endLocalDate)<=0){
                        addNew(weekday,title,"",new Time(startHour,startMinute),new Time(endHour,endMinute));
                    }else if(getDateDif(nowDate,endLocalDate)>0){
                        deleteSchedule(title,startDateSplit[0]+startDateSplit[1]+startDateSplit[2],endDateSplit[0]+endDateSplit[1]+endDateSplit[2],time);
                    }
                }
            }

        });

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getDateDif(LocalDate dgf, LocalDate target){
        return dgf.compareTo(target);
    }

    private void deletePlan(String title, String date, String time){
        planRef.child(FirstAuthActivity.getMyID()).child(date+"_"+time+"_"+title).setValue(null);
    }

    private void deleteSchedule(String title, String startDate,String endDate, String time){
        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDate+"~"+endDate+"_"+time+"_"+title).setValue(null);
    }

}
