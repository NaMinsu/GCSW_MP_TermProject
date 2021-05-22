package com.example.teamone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

public class addPlan extends Activity {

    Button startTimeBtn, endTimeBtn,startDateBtn, cancelBtn, addBtn;
    EditText planName;
    TextView startTimeTxt, endTimeTxt, startDateTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference planRef = database.getReference("plan");


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_add_plan);


        startTimeBtn = findViewById(R.id.startTimeBtn_plan);
        endTimeBtn = findViewById(R.id.endTimeBtn_plan);
        startDateBtn = findViewById(R.id.startDateBtn_plan);
        cancelBtn = findViewById(R.id.addPlanCancel);
        addBtn = findViewById(R.id.addPlanAdd);
        planName = findViewById(R.id.planNameTxt);
        startTimeTxt = findViewById(R.id.startTimeTxt_plan);
        endTimeTxt = findViewById(R.id.endTimeTxt_plan);
        startDateTxt = findViewById(R.id.startDateTxt_plan);

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        /*
        시작 시간 정하는 버튼입니다.
        TimePickerDialog을 통해 시간을 읽어오고
        3시면 03시로 표기, 3분이면 03분으로 표기할 수 있도록 만들었습니다.
        결과적으로 03:03으로 입력되게 했습니다.
         */
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(addPlan.this,
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
                                startTimeTxt.setText(hour + ":" + minute);
                            }

                        }, 0, 0, true);
                timePickerDialog.show();

            }
        });

        /*
        끝나는 시간을 정하는 버튼입니다.
        시작시간 버튼과 동일합니다.
         */
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(addPlan.this,
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

                                endTimeTxt.setText(hour + ":" + minute);

                            }

                        }, 0, 0, true);
                timePickerDialog.show();
            }
        });


        /*
        날짜를 정하는 버튼입니다.
        DatePickDialog를 통해 날짜를 불러오고
        월에 해당하는 부분이 1 적게 나와서 3월이면 2로 표기가 되어 +1을 했습니다.
        그외에 시간과 같이 3일은 03일 3월은 03월 처럼 십의 자리를 표기했습니다.
        년/월/일 형식으로 텍스트를 표기했습니다.
         */
        startDateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(addPlan.this,
                        new DatePickerDialog.OnDateSetListener() {
                            String month, day;

                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                i1 += 1;
                                if (i1 < 10) {
                                    month = "0" + Integer.toString(i1);
                                } else {
                                    month = Integer.toString(i1);
                                }
                                if (i2 < 10) {
                                    day = "0" + Integer.toString(i2);
                                } else {
                                    day = Integer.toString(i2);
                                }
                                startDateTxt.setText(i + "/" + month + "/" + day);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        /*
        취소 버튼 클릭시 해당 액티비티를 종료합니다.
         */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
        추가 버튼 클릭시 발생하는 이벤트입니다.
        각각의 일정 명, 시작 시간과 같은 정보를 문자열로 읽어오고,
        이름, 시간 등등 하나라도 입력하지 않은게 있다면 진행x
        일정 입력에 문제가 있는 사항이 있는지 없는지 파악하기 위해 boolean 타입의 correct 변수를 생성하여 true로 저장했습니다.
         */
        addBtn.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String planNameV = planName.getText().toString();
                String startTimeTxtV = startTimeTxt.getText().toString();
                String endTimeTxtV = endTimeTxt.getText().toString();
                String startDateTxtV = startDateTxt.getText().toString();

                if (planNameV.length() != 0 && !startTimeTxtV.equals("시작시간") && !endTimeTxtV.equals("종료시간") && !startDateTxtV.equals("계획일")&&!startDateTxtV.equals("일정 일")) {
                    int startHour, startMinute, endHour, endMinute;

                    String[] startTimeSplit = startTimeTxtV.split(":");
                    String[] endTimeSplit = endTimeTxtV.split(":");
                    String[] startDateSplit = startDateTxtV.split("/");

                    boolean correct = true;
                    int i = 0;

                    /*
                    시작 시간이 종료시간보다 늦을 경우 correct를 false로 변경합니다.
                     */
                    if (startTimeSplit[0].compareTo(endTimeSplit[0]) > 0) {
                        correct = false;
                    } else if(startTimeSplit[0].compareTo(endTimeSplit[0]) == 0){
                        if (startTimeSplit[1].compareTo(endTimeSplit[1]) > 0) {
                            correct = false;
                        }
                    }

                    if (correct) {

                        startHour = Integer.parseInt(startTimeSplit[0]);
                        startMinute = Integer.parseInt(startTimeSplit[1]);
                        endHour = Integer.parseInt(endTimeSplit[0]);
                        endMinute = Integer.parseInt(endTimeSplit[1]);

                        LocalTime start = LocalTime.of(startHour,startMinute);
                        LocalTime end = LocalTime.of(endHour,endMinute);

                        /*
                        겹치는 일정이 있는지 파악하기 위해 데이터베이스에서 일정을 모두 읽어옵니다.
                        하나라도 겹치는게 있다면 해당 일정은 데이터베이스로 들어가지 못하게 합니다.
                        우선 날짜가 겹치는 상황에서 시간이 겹치면 겹친다고 파악합니다.
                         */
                        planRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                            boolean isOverlap = false;

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {

                                String titleG,dateG,timeG;
                                int startHourG,startMinuteG,endHourG,endMinuteG;

                                for (DataSnapshot plan : task.getResult().getChildren()) {

                                    titleG = plan.child("title").getValue().toString();
                                    dateG = plan.child("date").getValue().toString();
                                    timeG = plan.child("time").getValue().toString();

                                    String[] timeSplitG = timeG.split("~");
                                    startHourG = Integer.parseInt(timeSplitG[0].substring(0,2));
                                    startMinuteG = Integer.parseInt(timeSplitG[0].substring(3,5));
                                    endHourG = Integer.parseInt(timeSplitG[1].substring(0,2));
                                    endMinuteG = Integer.parseInt(timeSplitG[1].substring(3,5));

                                    LocalTime startG = LocalTime.of(startHourG,startMinuteG);
                                    LocalTime endG = LocalTime.of(endHourG,endMinuteG);

                                    if(isOverlapDate(dateG,startDateTxtV)){
                                        if(isOverlapTime(start,end,startG,endG)){
                                            isOverlap = true;
                                            break;
                                        }
                                    }
                                }

                                /*
                                겹치는게 없다면 입력을 하는데, 데이터베이스에 입력되는 것보다
                                메인화면에서 불러오는게 더 빨라 입력한 일정이 나오지 않기 때문에
                                마지막 데이터가 저장되는것을 성공하면 해당 액티비티를 종료하는 것으로 했습니다.
                                 */
                                if(!isOverlap) {
                                    planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0] + startDateSplit[1] + startDateSplit[2] + "_" + startTimeTxtV + "~" + endTimeTxtV + "_" + planNameV).child("title").setValue(planNameV);
                                    planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0] + startDateSplit[1] + startDateSplit[2] + "_" + startTimeTxtV + "~" + endTimeTxtV + "_" + planNameV).child("date").setValue(startDateTxtV);
                                    planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0] + startDateSplit[1] + startDateSplit[2] + "_" + startTimeTxtV + "~" + endTimeTxtV + "_" + planNameV).child("time").setValue(startTimeTxtV + "~" + endTimeTxtV).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            finish();
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(), "겹치는 plan이 있습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "잘못된 시간/기간 입력 발생", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    /*
    일정은 기간이 따로 없고 해당 날짜에만 수행하기 때문에
    날짜가 같으면 true를 아니면 false를 리턴합니다.
     */
    private boolean isOverlapDate(String start,String startG){
        if (start.equals(startG)){
            return true;
        }else{
            return false;
        }
    }

    /*
    시간이 겹치는지 확인하는 함수인데
    A와 B라는 일정을 비교한다고 가정하겠습니다

    A가 B보다 시작시간이 빠르거나 같으면서 종료시간은 B보다 늦거나 같을 때 (A의 시간이 B를 감쌀 때)
    A의 시작시간이 B의 종료시간보다 빠르면서 A의 종료시간이 B의 종료시간보다 빠를 때
    (서로 교집합이 있는 상황에서 A가 더 앞에 있을 때)

    그리고 해당하는 상황에서 A와 B가 뒤바뀐 상황에 있으면 시간이 겹치는 것으로 했습니다.
     */
    private boolean isOverlapTime(LocalTime start,LocalTime end, LocalTime startG,LocalTime endG){
        if(start.compareTo(startG)<=0&&end.compareTo(endG)>=0){
            return true;
        }
        if(startG.compareTo(start)<=0&&endG.compareTo(end)>=0){
            return true;
        }
        if(start.compareTo(endG)<0&&end.compareTo(endG)>0){
            return true;
        }
        if(startG.compareTo(end)<0&&endG.compareTo(end)>0){
            return true;
        }
        return false;
    }


}