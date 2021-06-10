package com.gcsw.teamone;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class deleteSchedule extends Activity {

    private deleteScheduleAdapter adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = database.getReference("schedule");
    public static Context contextSchedule;

    String title, startDate, endDate, date, time, weekday;
    Button deleteScheduleCancel;
    TextView noSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_schedule);
        noSchedule = findViewById(R.id.noDeleteSchedule);
        contextSchedule = this;

        init();
        deleteScheduleCancel = findViewById(R.id.deleteScheduleCancel);
        deleteScheduleCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.deleteScheduleRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new deleteScheduleAdapter();
        recyclerView.setAdapter(adapter);
    }

    /*
    스케쥴이 있으면 스케쥴이 없다 텍스트를 안보이게 하고
    스케쥴이 없으면 스케쥴이 없다 텍스트를 보이게 하는 함수입니다.
     */
    /*
    if there isn't any schedule, make the text(noSchedule) invisible
    if there is any schedule, make the text(noSchedule) visible
     */
    public void findNoSchedule(){
        if(adapter.getItemCount()==0){
            noSchedule.setVisibility(View.VISIBLE);
        }else{
            noSchedule.setVisibility(View.GONE);
        }
    }

    private void getData(String title, String content, String time, String weekday) {

        todayScheduleDataItem data = new todayScheduleDataItem();
        data.setTitle(title);
        data.setContent(content);
        data.setTime(time);
        data.setWeekday(weekday);
        adapter.addItem(data);

        adapter.notifyDataSetChanged();
    }


    /*
    Everytime the activitiy is onResume,
    read schedules from data and enter it into recycler view
     */
    public void onResume() {
        super.onResume();
        if(connectStatus.getConnectivityStatus(getApplicationContext())!=3) {

            database.goOnline();

            adapter.remove();
            adapter.notifyDataSetChanged();

            scheduleRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(Task<DataSnapshot> task) {

                    for (DataSnapshot schedule : task.getResult().getChildren()) {
                        title = schedule.child("title").getValue().toString();
                        startDate = schedule.child("startDate").getValue().toString();
                        endDate = schedule.child("endDate").getValue().toString();
                        date = startDate + " ~ " + endDate;
                        time = schedule.child("time").getValue().toString();
                        weekday = getWeekday(Integer.parseInt(schedule.child("weekday").getValue().toString()));
                        getData(title, date, time, weekday);
                    }
                    findNoSchedule();
                }

            });
        }else{
            database.goOffline();
            Toast.makeText(getApplicationContext(),"인터넷이 연결되지 않았습니다",Toast.LENGTH_SHORT).show();
        }

    }

    private String getWeekday(int index) {
        String[] weekday = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        return weekday[index];
    }


}