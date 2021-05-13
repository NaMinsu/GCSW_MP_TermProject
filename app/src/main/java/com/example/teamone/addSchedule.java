package com.example.teamone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.teamone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class addSchedule extends Activity {

    Button startTimeBtn, endTimeBtn, weekdayLeftBtn, weekdayRightBtn, startDateBtn, endDateBtn, cancelBtn, addBtn;
    EditText scheduleName;
    TextView startTimeTxt, endTimeTxt, weekdayTxt, startDateTxt, endDateTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = database.getReference("schedule");
    int weekdayIndex;


    private DatePickerDialog.OnDateSetListener callbackMethod;

    String weekday[] = new String[7];

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_add_schedule);


        startTimeBtn = findViewById(R.id.startTimeBtn_main);
        endTimeBtn = findViewById(R.id.endTimeBtn_main);
        weekdayLeftBtn = findViewById(R.id.weekDayLeft);
        weekdayRightBtn = findViewById(R.id.weekDayRight);
        startDateBtn = findViewById(R.id.startDateBtn_main);
        endDateBtn = findViewById(R.id.endDateBtn_main);
        cancelBtn = findViewById(R.id.addScheduleCancel);
        addBtn = findViewById(R.id.addScheduleAdd);

        scheduleName = findViewById(R.id.scheduleNameTxt);

        startTimeTxt = findViewById(R.id.startTimeTxt_main);
        endTimeTxt = findViewById(R.id.endTimeTxt_main);
        weekdayTxt = findViewById(R.id.weekDayText);
        startDateTxt = findViewById(R.id.startDateTxt_main);
        endDateTxt = findViewById(R.id.endDateTxt_main);

        String[] weekday = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        weekdayIndex = 1;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(addSchedule.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(addSchedule.this,
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(addSchedule.this,
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
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            DatePickerDialog datePickerDialog;

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(addSchedule.this,
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
                                endDateTxt.setText(i + "/" + month + "/" + day);
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

                String scheduleNameV = scheduleName.getText().toString();
                String startTimeTxtV = startTimeTxt.getText().toString();
                String endTimeTxtV = endTimeTxt.getText().toString();
                String startDateTxtV = startDateTxt.getText().toString();
                String endDateTxtV = endDateTxt.getText().toString();

                if (scheduleNameV.length() != 0 && !startTimeTxtV.equals("시작시간") && !endTimeTxtV.equals("종료시간") && !startDateTxtV.equals("시작일") && !endDateTxtV.equals("종료일")) {
                    int startHour, startMinute, endHour, endMinute, startYear, startMonth, startDay, endYear, endMonth, endDay;

                    String[] startTimeSplit = startTimeTxtV.split(":");
                    String[] endTimeSplit = endTimeTxtV.split(":");
                    String[] startDateSplit = startDateTxtV.split("/");
                    String[] endDateSplit = endDateTxtV.split("/");

                    boolean correct = true;
                    int i = 0;
                    if (startTimeSplit[0].compareTo(endTimeSplit[0]) > 0) {
                        correct = false;
                    } else if(startTimeSplit[0].compareTo(endTimeSplit[0]) == 0){
                        if (startTimeSplit[1].compareTo(endTimeSplit[1]) > 0) {
                            correct = false;
                        }
                    }

                    if (startDateSplit[0].compareTo(endDateSplit[0]) > 0) {
                        correct = false;
                    } else if(startDateSplit[0].compareTo(endDateSplit[0]) == 0){
                        if (startDateSplit[1].compareTo(endDateSplit[1]) > 0) {
                            correct = false;
                        } else if(startDateSplit[1].compareTo(endDateSplit[1]) == 0){
                            if (startDateSplit[2].compareTo(endDateSplit[2]) > 0) {
                                correct = false;
                            }
                        }
                    }
                    if (startTimeSplit[0].compareTo("09") < 0) {
                        correct = false;
                        Toast.makeText(getApplicationContext(), "시간표를 벗어난 범위의 스케쥴", Toast.LENGTH_SHORT).show();
                    }
                    if (correct) {
                        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV).child("title").setValue(scheduleNameV);
                        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV).child("startDate").setValue(startDateTxtV);
                        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV).child("endDate").setValue(endDateTxtV);
                        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV).child("time").setValue(startTimeTxtV+"~"+endTimeTxtV);
                        scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV).child("weekday").setValue(Integer.toString(weekdayIndex));

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
