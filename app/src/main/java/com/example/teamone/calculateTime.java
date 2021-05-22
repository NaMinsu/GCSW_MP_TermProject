package com.example.teamone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class calculateTime extends AppCompatActivity {


    TextView minute, hour;
    Button hourUp, hourDown, minuteUp, minuteDown;
    CheckBox Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
    Button cancel, add;
    Boolean running;
    boolean[] weekday = new boolean[7];

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_time);

        minute = findViewById(R.id.groupScheduleMinuteTxt);
        hour = findViewById(R.id.groupScheduleHourTxt);
        hourUp = findViewById(R.id.groupScheduleHourUp);
        hourDown = findViewById(R.id.groupScheduleHourDown);
        minuteUp = findViewById(R.id.groupScheduleMinuteUp);
        minuteDown = findViewById(R.id.groupScheduleMinuteDown);
        Sunday = findViewById(R.id.groupSunday);
        Monday = findViewById(R.id.groupMonday);
        Tuesday = findViewById(R.id.groupTuesday);
        Wednesday = findViewById(R.id.groupWednesday);
        Thursday = findViewById(R.id.groupThursday);
        Friday = findViewById(R.id.groupFriday);
        Saturday = findViewById(R.id.groupSaturday);
        cancel = findViewById(R.id.addGroupScheduleCancel);
        add = findViewById(R.id.addGroupScheduleAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),groupTable.class);

                weekday[0] = Sunday.isChecked();
                weekday[1] = Monday.isChecked();
                weekday[2] = Tuesday.isChecked();
                weekday[3] = Wednesday.isChecked();
                weekday[4] = Thursday.isChecked();
                weekday[5] = Friday.isChecked();
                weekday[6] = Saturday.isChecked();


                int hourNum = Integer.parseInt(hour.getText().toString());
                int minuteNum = Integer.parseInt(minute.getText().toString());
                if(hourNum==0&&minuteNum==0) {
                    Toast.makeText(getApplicationContext(),"시간을 입력하세요",Toast.LENGTH_SHORT).show();
                }else{
                    intent.putExtra("weekday", weekday);
                    intent.putExtra("hour", hourNum);
                    intent.putExtra("minute", minuteNum);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        hourUp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Thread a = new HourUpThread();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        running = true;
                        a.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        running = false;
                        a.interrupt();
                        break;
                }
                return false;
            }
        });

        hourDown.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Thread a = new HourDownThread();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        running = true;
                        a.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        running = false;
                        a.interrupt();
                        break;
                }
                return false;
            }
        });

        minuteUp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Thread a = new MinuteUpThread();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        running = true;
                        a.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        running = false;
                        a.interrupt();
                        break;
                }
                return false;
            }
        });

        minuteDown.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Thread a = new MinuteDownThread();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        running = true;
                        a.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        running = false;
                        a.interrupt();
                        break;
                }
                return false;
            }
        });


    }

    class HourUpThread extends Thread {
        public void run() {
            int i = 0;
            while (running) {
                try {
                    goHourUp();
                    if (i < 2) {
                        Thread.sleep(300);
                    } else if (i < 4) {
                        Thread.sleep(150);
                    } else if (i < 6) {
                        Thread.sleep(75);
                    }else{
                        Thread.sleep(30);
                    }
                    i++;
                } catch (Exception ex) {
                }
            }
        }
    }
    class HourDownThread extends Thread {
        public void run() {
            int i = 0;
            while (running) {
                try {
                    goHourDown();
                    if (i < 2) {
                        Thread.sleep(300);
                    } else if (i < 4) {
                        Thread.sleep(150);
                    } else if (i < 6) {
                        Thread.sleep(75);
                    }else{
                        Thread.sleep(30);
                    }
                    i++;
                } catch (Exception ex) {
                }
            }
        }
    }
    class MinuteUpThread extends Thread {
        public void run() {
            int i = 0;
            while (running) {
                try {
                    goMinuteUp();
                    if (i < 3) {
                        Thread.sleep(300);
                    } else if (i < 5) {
                        Thread.sleep(200);
                    } else if (i < 8) {
                        Thread.sleep(120);
                    } else{
                        Thread.sleep(50);
                    }
                    i++;
                } catch (Exception ex) {
                }
            }
        }
    }
    class MinuteDownThread extends Thread {
        public void run() {
            int i = 0;
            while (running) {
                try {
                    goMinuteDown();
                    if (i < 3) {
                        Thread.sleep(300);
                    } else if (i < 5) {
                        Thread.sleep(200);
                    } else if (i < 8) {
                        Thread.sleep(120);
                    } else{
                        Thread.sleep(50);
                    }
                    i++;
                } catch (Exception ex) {
                }
            }
        }
    }



    public Boolean goHourUp() {
        int H = Integer.parseInt(hour.getText().toString());
        if (H >= 14) {
            return false;
        } else {
            hour.setText(Integer.toString(H + 1));
            return true;
        }
    }

    public Boolean goHourDown() {
        int H = Integer.parseInt(hour.getText().toString());
        if (H == 0) {
            return false;
        } else {
            hour.setText(Integer.toString(H - 1));
            return true;
        }
    }

    public void goMinuteUp() {
        int H = Integer.parseInt(minute.getText().toString());
        if (H >= 59) {
            if(goHourUp()) {
                minute.setText(Integer.toString(0));
            }
        } else {
            minute.setText(Integer.toString(H + 1));
        }
    }

    public void goMinuteDown() {
        int H = Integer.parseInt(minute.getText().toString());
        if (H == 0) {
            if(goHourDown()) {
                minute.setText(Integer.toString(59));
            }
        } else {
            minute.setText(Integer.toString(H - 1));
        }
    }

}