package com.example.teamone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class addPlan extends Activity {

    Button startTimeBtn, endTimeBtn, weekdayLeftBtn, weekdayRightBtn, startDateBtn, cancelBtn, addBtn;
    EditText planName;
    TextView startTimeTxt, endTimeTxt, weekdayTxt, startDateTxt;
    TimePickerDialog timePickerDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference planRef = database.getReference("plan");
    int weekdayIndex;

    private DatePickerDialog.OnDateSetListener callbackMethod;

    String weekday[] = new String[7];

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_add_plan);


        startTimeBtn = findViewById(R.id.startTimeBtn_plan);
        endTimeBtn = findViewById(R.id.endTimeBtn_plan);

        weekdayLeftBtn = findViewById(R.id.weekDayLeft_plan);
        weekdayRightBtn = findViewById(R.id.weekDayRight_plan);

        startDateBtn = findViewById(R.id.startDateBtn_plan);
        cancelBtn = findViewById(R.id.addPlanCancel);
        addBtn = findViewById(R.id.addPlanAdd);

        planName = findViewById(R.id.planNameTxt);

        startTimeTxt = findViewById(R.id.startTimeTxt_plan);
        endTimeTxt = findViewById(R.id.endTimeTxt_plan);

        weekdayTxt = findViewById(R.id.weekDayText_plan);

        startDateTxt = findViewById(R.id.startDateTxt_plan);

        String[] weekday = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        weekdayIndex = 1;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


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
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(addPlan.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            String hour, minute;

                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                if (i == 0 && i1 == 0) {
                                    i = 24;
                                }
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
        weekdayLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekdayIndex == 0) {
                    weekdayIndex = 6;
                } else {
                    weekdayIndex -= 1;
                }
                weekdayTxt.setText(weekday[weekdayIndex]);
            }
        });
        weekdayRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekdayIndex == 6) {
                    weekdayIndex = 0;
                } else {
                    weekdayIndex += 1;
                }
                weekdayTxt.setText(weekday[weekdayIndex]);
            }
        });
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            DatePickerDialog datePickerDialog;

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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                String planNameV = planName.getText().toString();
                String startTimeTxtV = startTimeTxt.getText().toString();
                String endTimeTxtV = endTimeTxt.getText().toString();
                String startDateTxtV = startDateTxt.getText().toString();

                if (planNameV.length() != 0 && !startTimeTxtV.equals("시작시간") && !endTimeTxtV.equals("종료시간") && !startDateTxtV.equals("계획일")) {
                    int startHour, startMinute, endHour, endMinute, startYear, startMonth, startDay, endYear, endMonth, endDay;

                    String[] startTimeSplit = startTimeTxtV.split(":");
                    String[] endTimeSplit = endTimeTxtV.split(":");
                    String[] startDateSplit = startDateTxtV.split("/");

                    boolean correct = true;
                    int i = 0;
                    if (startTimeSplit[0].compareTo(endTimeSplit[0]) > 0) {
                        correct = false;
                    } else if(startTimeSplit[0].compareTo(endTimeSplit[0]) == 0){
                        if (startTimeSplit[1].compareTo(endTimeSplit[1]) > 0) {
                            correct = false;
                        }
                    }

                    if (correct) {
                        planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+planNameV).child("title").setValue(planNameV);
                        planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+planNameV).child("date").setValue(startDateTxtV);
                        planRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+planNameV).child("time").setValue(startTimeTxtV+"~"+endTimeTxtV);

                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "잘못된 시간/기간 입력 발생", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

}