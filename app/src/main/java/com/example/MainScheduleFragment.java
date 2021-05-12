package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamone.R;
import com.example.teamone.Schedule;
import com.example.teamone.Time;
import com.example.teamone.TimetableView;
import com.example.teamone.addPlan;
import com.example.teamone.addSchedule;
import com.example.teamone.todaySchedule;
import com.example.teamone.todayScheduleData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainScheduleFragment extends Fragment {

    TimetableView timetable;
    private todaySchedule adapter;
    int numOfPlan = 0;

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
                startActivityForResult(intent,0);
            }
        });

        Button addPlan = v.findViewById(R.id.addPlanBtn);

        addPlan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.teamone.addPlan.class);
                startActivityForResult(intent,1);
            }
        });

        return v;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
                Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
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
}
