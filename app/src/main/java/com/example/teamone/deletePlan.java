package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class deletePlan extends Activity {

    public static Object contextSchedule;
    private deletePlanAdapter adapter;
    public static Context contextPlan;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference planRef = database.getReference("plan");
    ArrayList<todayScheduleData> plans = new ArrayList<todayScheduleData>();
    LinearLayout LL;

    TextView noPlan;
    String title, date, time;
    Button deletePlanCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_plan);
        contextPlan = this;

        init();

        noPlan = findViewById(R.id.noDeletePlan);
        deletePlanCancel = findViewById(R.id.deletePlanCancel);
        deletePlanCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.deletePlanRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new deletePlanAdapter();
        recyclerView.setAdapter(adapter);
    }

    /*
    계획이 있으면 계획이 없다 텍스트를 안보이게 하고
    계획이 없으면 계획이 없다 텍스트를 보이게 하는 함수입니다.
     */
    /*
    if there isn't any plan, make the text(noPlan) invisible
    if there is any plan, make the text(noPlan) visible
     */
    public void findNoPlan(){
        if(adapter.getItemCount()==0){
            noPlan.setVisibility(View.VISIBLE);
        }else{
            noPlan.setVisibility(View.GONE);
        }
    }


    private void getData(String title, String content, String time) {

        todayScheduleData data = new todayScheduleData();

        data.setTitle(title);
        data.setContent(content);
        data.setTime(time);
        adapter.addItem(data);
        adapter.notifyDataSetChanged();
    }


    /*
    Resume할 때 plan을 읽어와서 올리기
    (인터넷 연결되어있다면)
     */
    /*
    Everytime the activitiy is onResume,
    read plans from data and enter it into recycler view
     */
    public void onResume() {
        super.onResume();
        if(connectStatus.getConnectivityStatus(getApplicationContext())!=3) {

            database.goOnline();

        adapter.remove();
        adapter.notifyDataSetChanged();

        planRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                for (DataSnapshot plan : task.getResult().getChildren()) {
                    title = plan.child("title").getValue().toString();
                    date = plan.child("date").getValue().toString();
                    time = plan.child("time").getValue().toString();
                    getData(title, date, time);
                }
                findNoPlan();
            }
        });
        }else{
            database.goOffline();
            Toast.makeText(getApplicationContext(),"인터넷이 연결되지 않았습니다",Toast.LENGTH_SHORT).show();
        }
    }


}