package com.example.teamone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

/*
main fragment(Main Screen)
this shows plans and timetable
 */
public class FragmentSchedule extends Fragment {

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

    MediaPlayer soundReload;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_schedules,container,false);

        init(v);

        soundReload = MediaPlayer.create(getContext(),R.raw.graph);

        timetable = v.findViewById(R.id.timetable);
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });

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

        Button deletePlan = v.findViewById(R.id.deletePlanBtn);

        deletePlan.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), com.example.teamone.deletePlan.class);
                startActivity(intent);
            }
        });

        Button deleteSchedule = v.findViewById(R.id.deleteScheduleBtn);

        deleteSchedule.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), com.example.teamone.deleteSchedule.class);
                startActivity(intent);
            }
        });

        Button refresh = v.findViewById(R.id.refreshBtn);
        refresh.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view){
                onResume();
            }
        });

        return v;
    }




    /*
    add new plan or schedule into timetable
     */
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

    /*
    add new plan into plan recycler view
     */
    private void getData(String title, String content, String time){

        numOfPlan++;

        todayScheduleData data = new todayScheduleData();

        data.setTitle(title);
        data.setContent(content);
        data.setTime(time);
        adapter.addItem(data);

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

    /*
    onResume 할 때 매번 일정과 스케쥴을 다시 읽어들입니다.
    다시 읽어들이는 과정에서 각각 시간이 지난 일정과 스케쥴은 모두 삭제합니다.
    일정이나 스케쥴은 당일 이전이면 삭제합니다.
    또한 당일 ~ 6일뒤 사이라면 시간표에 표시합니다.
     */
    /*
    I read the schedule and schedule again every time I do on Resume.
    In the process of re-reading, delete all plans and schedules that have passed their time.
    If the plan or schedule is before the day, delete it.
    It is also displayed in the timetable if it is between the same day and six days later.    */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume(){
        super.onResume();
        timetable.setHeaderHighlightDefault();
        timetable.setHeaderHighlight(getHeaderIndex(LocalDate.now().getDayOfWeek().toString()));
        /*
        LTE와 WIFI 둘 중 하나라도 연결되어있다면
         */
        if(connectStatus.getConnectivityStatus(getContext())!=3) {

            adapter.remove();
            adapter.notifyDataSetChanged();
            timetable.removeAll();
            database.goOnline();

            LocalDate nowDate = LocalDate.now();
            LocalDate nextDate = LocalDate.now().plusDays(6);
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
                        startHour = Integer.parseInt(timeSplit[0].substring(0, 2));
                        startMinute = Integer.parseInt(timeSplit[0].substring(3, 5));
                        endHour = Integer.parseInt(timeSplit[1].substring(0, 2));
                        endMinute = Integer.parseInt(timeSplit[1].substring(3, 5));

                        String[] dateSplit = date.split("/");
                        LocalDate tmpDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
                        weekday = getWeekdayIndex(tmpDate.getDayOfWeek().toString());

                        if (getDateDif(tmpDate, nowDate) < 0) {
                            deletePlan(title, dateSplit[0] + dateSplit[1] + dateSplit[2], time);
                        } else if (getDateDif(tmpDate, nowDate) > 0 && getDateDif(tmpDate, nextDate) < 0) {
                            addNew(weekday, title, "", new Time(startHour, startMinute), new Time(endHour, endMinute));
                            getData(title, date, time);
                        } else {
                            getData(title, date, time);
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
                        startHour = Integer.parseInt(timeSplit[0].substring(0, 2));
                        startMinute = Integer.parseInt(timeSplit[0].substring(3, 5));
                        endHour = Integer.parseInt(timeSplit[1].substring(0, 2));
                        endMinute = Integer.parseInt(timeSplit[1].substring(3, 5));

                        String[] startDateSplit = startDate.split("/");
                        String[] endDateSplit = endDate.split("/");
                        LocalDate startLocalDate = LocalDate.of(Integer.parseInt(startDateSplit[0]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[2]));
                        LocalDate endLocalDate = LocalDate.of(Integer.parseInt(endDateSplit[0]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[2]));
                        LocalDate tmpDate = nowDate.with(TemporalAdjusters.nextOrSame(getDate(weekday)));

                        if (getDateDif(tmpDate, startLocalDate) >= 0 && getDateDif(tmpDate, endLocalDate) <= 0) {
                            addNew(weekday, title, "", new Time(startHour, startMinute), new Time(endHour, endMinute));
                        } else if (getDateDif(tmpDate, endLocalDate) > 0) {
                            deleteSchedule(title, startDateSplit[0] + startDateSplit[1] + startDateSplit[2], endDateSplit[0] + endDateSplit[1] + endDateSplit[2], time, Integer.toString(weekday));
                        }
                    }
                }
            });

            soundReload.start();
        }
        else{
            database.goOffline();
            Toast.makeText(getContext(),"인터넷이 연결되지 않았습니다",Toast.LENGTH_SHORT).show();
        }

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
    public int getHeaderIndex(String weekday){
        if(weekday.equals("SUNDAY")){
            return 1;
        }else if(weekday.equals("MONDAY")){
            return 2;
        }else if(weekday.equals("TUESDAY")){
            return 3;
        }else if(weekday.equals("WEDNESDAY")){
            return 4;
        }else if(weekday.equals("THURSDAY")){
            return 5;
        }else if(weekday.equals("FRIDAY")){
            return 6;
        }else if(weekday.equals("SATURDAY")){
            return 7;
        }else{
            return 0;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DayOfWeek getDate(int weekday){
        if(weekday == 0){
            return DayOfWeek.SUNDAY;
        }else if(weekday == 1){
            return DayOfWeek.MONDAY;
        }else if(weekday == 2){
            return DayOfWeek.TUESDAY;
        }else if(weekday == 3){
            return DayOfWeek.WEDNESDAY;
        }else if(weekday == 4){
            return DayOfWeek.THURSDAY;
        }else if(weekday == 5){
            return DayOfWeek.FRIDAY;
        }else if(weekday == 6){
            return DayOfWeek.SATURDAY;
        }
        return DayOfWeek.SUNDAY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getDateDif(LocalDate dgf, LocalDate target){
        return dgf.compareTo(target);
    }

    private void deletePlan(String title, String date, String time){
        planRef.child(FirstAuthActivity.getMyID()).child(date+"_"+time+"_"+title).setValue(null);
    }

    private void deleteSchedule(String title, String startDate,String endDate, String time,String weekdayIndex){
        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDate+"~"+endDate+"_"+time+"_"+title+"_"+weekdayIndex).setValue(null);
    }

}
