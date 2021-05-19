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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class addSchedule extends Activity {

    Button startTimeBtn, endTimeBtn, weekdayLeftBtn, weekdayRightBtn, startDateBtn, endDateBtn, cancelBtn, addBtn;
    EditText scheduleName;
    TextView startTimeTxt, endTimeTxt, weekdayTxt, startDateTxt, endDateTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = database.getReference("schedule");
    int weekdayIndex;

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


        /*
        시작 시간 정하는 버튼입니다.
        TimePickerDialog을 통해 시간을 읽어오고
        3시면 03시로 표기, 3분이면 03분으로 표기할 수 있도록 만들었습니다.
        결과적으로 03:03으로 입력되게 했습니다.
         */
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

         /*
        끝나는 시간을 정하는 버튼입니다.
        시작시간 버튼과 동일합니다.
         */
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
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

                                endTimeTxt.setText(hour + ":" + minute);

                            }

                        }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        /*
        스케쥴의 요일을 변경하는 버튼입니다.
        left는 왼쪽 방향, right는 오른쪽 방향으로 바꿉니다.
         */
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

        /*
        시작 날짜를 정하는 버튼입니다.
        DatePickDialog를 통해 날짜를 불러오고
        월에 해당하는 부분이 1 적게 나와서 3월이면 2로 표기가 되어 +1을 했습니다.
        그외에 시간과 같이 3일은 03일 3월은 03월 처럼 십의 자리를 표기했습니다.
        년/월/일 형식으로 텍스트를 표기했습니다.
         */
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
        /*
        종료 날짜를 정하는 버튼입니다.
        시작 날짜와 동일하게 작동합니다.
         */
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
        각각의 스케쥴 명, 시작 시간과 같은 정보를 문자열로 읽어오고,
        이름, 시간 등등 하나라도 입력하지 않은게 있다면 진행x
        일정 입력에 문제가 있는 사항이 있는지 없는지 파악하기 위해 boolean 타입의 correct 변수를 생성하여 true로 저장했습니다.
         */
        addBtn.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String scheduleNameV = scheduleName.getText().toString();
                String startTimeTxtV = startTimeTxt.getText().toString();
                String endTimeTxtV = endTimeTxt.getText().toString();
                String startDateTxtV = startDateTxt.getText().toString();
                String endDateTxtV = endDateTxt.getText().toString();

                if (scheduleNameV.length() != 0 && !startTimeTxtV.equals("시작시간") && !endTimeTxtV.equals("종료시간") && !startDateTxtV.equals("시작일") && !endDateTxtV.equals("종료일")) {

                    String[] startTimeSplit = startTimeTxtV.split(":");
                    String[] endTimeSplit = endTimeTxtV.split(":");
                    String[] startDateSplit = startDateTxtV.split("/");
                    String[] endDateSplit = endDateTxtV.split("/");

                    boolean correct = true;

                    /*
                    시작 시간이나 시작 날짜가 종료 시간이나 종료 날짜보다 늦다면 correct를 false로 만듭니다.
                     */
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

                    /*
                    스케쥴은 오전9시부터 시작하기 때문에 9시 이전에 시작하는 스케쥴이라면 false로합니다.
                     */
                    if (startTimeSplit[0].compareTo("09") < 0) {
                        correct = false;
                        Toast.makeText(getApplicationContext(), "시간표를 벗어난 범위의 스케쥴", Toast.LENGTH_SHORT).show();
                    }
                    if (correct) {

                        LocalDate startDate = LocalDate.of(Integer.parseInt(startDateSplit[0]),Integer.parseInt(startDateSplit[1]),Integer.parseInt(startDateSplit[2]));
                        LocalDate endDate = LocalDate.of(Integer.parseInt(endDateSplit[0]),Integer.parseInt(endDateSplit[1]),Integer.parseInt(endDateSplit[2]));
                        LocalTime startTime = LocalTime.of(Integer.parseInt(startTimeSplit[0]),Integer.parseInt(startTimeSplit[1]));
                        LocalTime endTime = LocalTime.of(Integer.parseInt(endTimeSplit[0]),Integer.parseInt(endTimeSplit[1]));


                        /*
                        이미 있는 스케쥴과 겹치는 스케쥴인지 확인하게 위해 데이터베이스에서 스케쥴을 모두 읽어옵니다.
                         */
                        scheduleRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                            boolean isOverlap = false;

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {

                                String titleG,dateG,timeG,startDateG,endDateG;
                                int startHourG,startMinuteG,endHourG,endMinuteG,weekdayG;

                                for (DataSnapshot plan : task.getResult().getChildren()) {

                                    titleG = plan.child("title").getValue().toString();
                                    startDateG = plan.child("startDate").getValue().toString();
                                    endDateG = plan.child("endDate").getValue().toString();
                                    timeG = plan.child("time").getValue().toString();
                                    weekdayG = Integer.parseInt(plan.child("weekday").getValue().toString());


                                    String[] timeSplitG = timeG.split("~");
                                    startHourG = Integer.parseInt(timeSplitG[0].substring(0,2));
                                    startMinuteG = Integer.parseInt(timeSplitG[0].substring(3,5));
                                    endHourG = Integer.parseInt(timeSplitG[1].substring(0,2));
                                    endMinuteG = Integer.parseInt(timeSplitG[1].substring(3,5));

                                    String[] startDateSplitG = startDateG.split("/");
                                    String[] endDateSplitG = endDateG.split("/");

                                    LocalDate startDateGl = LocalDate.of(Integer.parseInt(startDateSplitG[0]),Integer.parseInt(startDateSplitG[1]),Integer.parseInt(startDateSplitG[2]));
                                    LocalDate endDateGl = LocalDate.of(Integer.parseInt(endDateSplitG[0]),Integer.parseInt(endDateSplitG[1]),Integer.parseInt(endDateSplitG[2]));

                                    LocalTime startTimeGl = LocalTime.of(startHourG,startMinuteG);
                                    LocalTime endTimeGl = LocalTime.of(endHourG,endMinuteG);

                                    /*
                                    읽어온 스케쥴과 비교를 하나씩 할 때 스케쥴 기간이 겹치는 상황에서
                                    시간이 겹치면 overlap을 true로 합니다.
                                     */
                                    if(isOverlapDate(startDate,endDate,startDateGl,endDateGl)){
                                        if(weekdayG==weekdayIndex) {
                                            if (isOverlapTime(startTime, endTime, startTimeGl, endTimeGl)) {
                                                isOverlap = true;
                                                break;
                                            }
                                        }
                                    }


                                }

                                /*
                                겹치는게 없다면 입력을 하는데, 데이터베이스에 입력되는 것보다
                                메인화면에서 불러오는게 더 빨라 입력한 스케쥴이 나오지 않기 때문에
                                마지막 데이터가 저장되는것을 성공하면 해당 액티비티를 종료하는 것으로 했습니다.
                                 */
                                if(!isOverlap) {
                                    scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV+"_"+weekdayIndex).child("title").setValue(scheduleNameV);
                                    scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV+"_"+weekdayIndex).child("startDate").setValue(startDateTxtV);
                                    scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV+"_"+weekdayIndex).child("endDate").setValue(endDateTxtV);
                                    scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV+"_"+weekdayIndex).child("time").setValue(startTimeTxtV+"~"+endTimeTxtV);
                                    scheduleRef.child(FirstAuthActivity.getMyID()).child(startDateSplit[0]+startDateSplit[1]+startDateSplit[2]+"~"+endDateSplit[0]+endDateSplit[1]+endDateSplit[2]+"_"+startTimeTxtV+"~"+endTimeTxtV+"_"+scheduleNameV+"_"+weekdayIndex).child("weekday").setValue(Integer.toString(weekdayIndex)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            finish();
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(), "겹치는 schedule이 있습니다.", Toast.LENGTH_SHORT).show();

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
    날짜가 겹치는지 확인하는 함수인데
    A와 B라는 일정을 비교한다고 가정하겠습니다

    A가 B보다 시작시간이 빠르거나 같으면서 종료시간은 B보다 늦거나 같을 때 (A의 시간이 B를 감쌀 때)
    A의 시작시간이 B의 종료시간보다 빠르거나 같으면서 A의 종료시간이 B의 종료시간보다 빠르거나 같을 때
    (서로 교집합이 있는 상황에서 A가 더 앞에 있을 때)

    그리고 해당하는 상황에서 A와 B가 뒤바뀐 상황에 있으면 시간이 겹치는 것으로 했습니다.

    시간과는 다르게 두번째 상황에서 >가 아닌 >=를 사용한 이유는 시간의 경우
    11시에 시작해서 12시에 끝나고 12시에 시작해서 13시에 끝나는 일정의 경우 겹친다고 보지 않지만
    11일에 시작해서 12일에 끝나고 12일에 시작해서 13일에 끝나는 일정은 12일에 겹치기 때문입니다.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isOverlapDate(LocalDate start, LocalDate end, LocalDate startG, LocalDate endG){
        if(start.compareTo(startG)<=0&&end.compareTo(endG)>=0){
            return true;
        }
        if(startG.compareTo(start)<=0&&endG.compareTo(end)>=0){
            return true;
        }
        if(start.compareTo(endG)<=0&&end.compareTo(endG)>=0){
            return true;
        }
        if(startG.compareTo(end)<=0&&endG.compareTo(end)>=0){
            return true;
        }
        return false;
    }

    /*
    시간이 겹치는지 확인하는 함수인데
    A와 B라는 일정을 비교한다고 가정하겠습니다

    A가 B보다 시작시간이 빠르거나 같으면서 종료시간은 B보다 늦거나 같을 때 (A의 시간이 B를 감쌀 때)
    A의 시작시간이 B의 종료시간보다 빠르면서 A의 종료시간이 B의 종료시간보다 빠를 때
    (서로 교집합이 있는 상황에서 A가 더 앞에 있을 때)

    그리고 해당하는 상황에서 A와 B가 뒤바뀐 상황에 있으면 시간이 겹치는 것으로 했습니다.
     */
    private boolean isOverlapTime(LocalTime start, LocalTime end, LocalTime startG, LocalTime endG){
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
