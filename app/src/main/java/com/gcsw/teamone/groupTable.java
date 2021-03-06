package com.gcsw.teamone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class groupTable extends AppCompatActivity {

    TimetableView timetable;
    private todaySchedule adapter;
    private FirebaseFunctions mFunctions;
    FirebaseDatabase Database  =FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = Database.getReference("schedule");
    DatabaseReference groupRef = Database.getReference("grouplist");
    DatabaseReference planRef = Database.getReference("plan");
    DatabaseReference usersRef = Database.getReference("users");
    ArrayList<String> WantPushMembers;
    ArrayList<String> membersToken;
    ArrayList<String> members;
    String name;
    String groupCode;
    ArrayList<Schedule> total;

    String planName = "not yet !@!@#@$";
    boolean[] weekdayTrue = new boolean[7];
    int hourInt, minuteInt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouptable);
        View selfLayout = (View) findViewById(R.id.gtLayout);
        mFunctions = FirebaseFunctions.getInstance();
        WantPushMembers = new ArrayList<>();
        membersToken = new ArrayList<>();
        members = new ArrayList<>();
        total = new ArrayList<>();
        LocalDate nowDate = LocalDate.now();
        LocalDate sunday = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate satday = nowDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        LocalDate nextDate = LocalDate.now().plusDays(6);

        timetable = (TimetableView) findViewById(R.id.timetable_group);

        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // ...
            }
        });
        timetable.setHeaderHighlightDefault();
        timetable.setHeaderHighlight(getHeaderIndex(LocalDate.now().getDayOfWeek().toString()));

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        groupCode = intent.getStringExtra("code");

        TextView names = (TextView)findViewById(R.id.grouptableName);
        names.setText(name);

        //read group schedule
        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.getResult().getValue().toString().equals("0")) {
                    String s = task.getResult().getValue().toString();
                    String[] fortable = s.split("!/");
                    String[] starttime = fortable[2].split(":");
                    String[] Endtime = fortable[3].split(":");
                    String[] date = fortable[4].split("/");

                    LocalDate scheduleDate = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

                    if (getDateDif(scheduleDate, nowDate) < 0) {
                        deleteGroupPlan();
                    } else if (getDateDif(scheduleDate, nowDate) > 0 && getDateDif(scheduleDate, nextDate) < 0) {
                        addNew(Integer.parseInt(fortable[0]), fortable[1], "", new Time(Integer.parseInt(starttime[0]), Integer.parseInt(starttime[1])), new Time(Integer.parseInt(Endtime[0]), Integer.parseInt(Endtime[1])));
                        setColor(planName);
                    }
                    planName = fortable[1];
                }

            }
        });//read group's meeting schedule

        //read member's schedules
        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot member : task.getResult().getChildren()) {
                    String Want = member.child("WantPush").getValue().toString();
                    if(Want.equals("1")){ WantPushMembers.add(member.getKey()); } // Want Push User (for Push on/off)

                    members.add(member.getKey());
                    scheduleRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for (DataSnapshot schedules : task.getResult().getChildren()) {
                                String a = schedules.getKey();
                                scheduleRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String title = task.getResult().child("title").getValue().toString();
                                        String weekday = task.getResult().child("weekday").getValue().toString();
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String[] times = Time.split("~");
                                        String[] startTime = times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        String startDate = task.getResult().child("startDate").getValue().toString();
                                        String endDate = task.getResult().child("endDate").getValue().toString();
                                        String[] startDateSplit = startDate.split("/");
                                        String[] endDateSplit = endDate.split("/");

                                        LocalDate startLocalDate = LocalDate.of(Integer.parseInt(startDateSplit[0]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[2]));
                                        LocalDate endLocalDate = LocalDate.of(Integer.parseInt(endDateSplit[0]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[2]));
                                        LocalDate tmpDate = nowDate.with(TemporalAdjusters.nextOrSame(getDatePersonal(Integer.parseInt(weekday))));

                                        if (getDateDif(tmpDate, startLocalDate) >= 0 && getDateDif(tmpDate, endLocalDate) <= 0) {
                                            addNew(Integer.parseInt(weekday), "", "", new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])), new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                            setColor(planName);
                                        } else if (getDateDif(tmpDate, endLocalDate) > 0) {
                                            deleteSchedule(title, startDateSplit[0] + startDateSplit[1] + startDateSplit[2], endDateSplit[0] + endDateSplit[1] + endDateSplit[2], Time, weekday,member.getKey());
                                        }

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        //read member's plan & show only plan in 7 days
        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot member : task.getResult().getChildren()) {
                    planRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for (DataSnapshot plan : task.getResult().getChildren()) {
                                String a = plan.getKey();
                                planRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String title = plan.child("title").getValue().toString();
                                        if (!title.equals(planName)) {
                                            String date = plan.child("date").getValue().toString();
                                            String time = plan.child("time").getValue().toString();
                                            String[] timeSplitPlan = time.split("~");
                                            int startHour = Integer.parseInt(timeSplitPlan[0].substring(0, 2));
                                            int startMinute = Integer.parseInt(timeSplitPlan[0].substring(3, 5));
                                            int endHour = Integer.parseInt(timeSplitPlan[1].substring(0, 2));
                                            int endMinute = Integer.parseInt(timeSplitPlan[1].substring(3, 5));

                                            String[] dateSplitPlan = date.split("/");
                                            LocalDate tmpDatePlan = LocalDate.of(Integer.parseInt(dateSplitPlan[0]), Integer.parseInt(dateSplitPlan[1]), Integer.parseInt(dateSplitPlan[2]));
                                            int weekday = getWeekdayIndex(tmpDatePlan.getDayOfWeek().toString());

                                            if (getDateDif(tmpDatePlan, nowDate) < 0) {
                                                deletePlan(title, dateSplitPlan[0] + dateSplitPlan[1] + dateSplitPlan[2], time,member.getKey());
                                            } else if (getDateDif(tmpDatePlan, nowDate) > 0 && getDateDif(tmpDatePlan, nextDate) < 0) {
                                                addNew(weekday, "", "", new Time(startHour, startMinute), new Time(endHour, endMinute));
                                                setColor(planName);
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    });

                }

            }
        });
      //read User's info
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                for (String GroupMember : WantPushMembers) {
                    if (task.getResult().child(GroupMember).hasChild("token")) {
                        membersToken.add(task.getResult().child(GroupMember).child("token").getValue().toString());
                    }
                }
            }
        });

        //Above reading from DB is done only 1 time. So if someone change their schedule or add new schedule, user need to
        //user need to reset to call that data
        Button resetB = (Button) findViewById(R.id.reset);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                timetable.setHeaderHighlightDefault();
                timetable.setHeaderHighlight(getHeaderIndex(LocalDate.now().getDayOfWeek().toString()));
            }
        });

        Button cancel = findViewById(R.id.cancel_groupMeeting);
        cancel.setOnClickListener(new View.OnClickListener(){
            String s;
            String[] fortable;
            String[] starttime;
            String[] Endtime;
            String[] date;
            boolean correct;
            @Override
            public void onClick(View view) {

                correct =false;

                //read group schedule and if there is group schedule, delete each user's plan(group schedule)
                groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                        if (!task.getResult().getValue().toString().equals("0")) {
                            s = task.getResult().getValue().toString();
                            fortable = s.split("!/");
                            starttime = fortable[2].split(":");
                            Endtime = fortable[3].split(":");
                            date = fortable[4].split("/");
                            deleteGroupPlan();
                            correct=true;
                            planName = "not yet !@!@#@$";
                        }

                    }
                });


                groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        for (DataSnapshot member : task.getResult().getChildren()) {
                            planRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if(correct) {
                                        String title = fortable[1];
                                        String startTime = starttime[0] + ":" + starttime[1];
                                        startTime = convertPlanTime(startTime);
                                        String endTime = Endtime[0] + ":" + Endtime[1];
                                        endTime = convertPlanTime(endTime);
                                        String time = startTime + "~" + endTime;
                                        String[] dates = convertPlanDate(date[0] + "/" + date[1] + "/" + date[2]);
                                        deletePlan(title, dates[0] + dates[1] + dates[2], time, member.getKey());
                                    }
                                }
                            });
                        }
                    }
                });




            }
        });

        Button goBack = (Button)selfLayout.findViewById(R.id.btnBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment","1");
                startActivity(intent);
            }
        });//fragment "1" is grouplist fragment

        Button calculating = (Button) findViewById(R.id.calculate);
        calculating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), calculateTime.class);
                startActivityForResult(intent, 0);
            }
        });//setting rule of meeting and calculating group's meeting


        //send group name and groupcode to add new member in this group
        Button AddMember = (Button) selfLayout.findViewById(R.id.btnAddMember);
        AddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupMemberAdder.class);
                intent.putExtra("name", name);
                intent.putExtra("code", groupCode);
                startActivity(intent);
            }
        });//

        AddMember.setSelected(true);
        cancel.setSelected(true);
        resetB.setSelected(true);
        calculating.setSelected(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DayOfWeek getDate(int weekday) {
        if (weekday == 6) {
            return DayOfWeek.SUNDAY;
        } else if (weekday == 0) {
            return DayOfWeek.MONDAY;
        } else if (weekday == 1) {
            return DayOfWeek.TUESDAY;
        } else if (weekday == 2) {
            return DayOfWeek.WEDNESDAY;
        } else if (weekday == 3) {
            return DayOfWeek.THURSDAY;
        } else if (weekday == 4) {
            return DayOfWeek.FRIDAY;
        } else if (weekday == 5) {
            return DayOfWeek.SATURDAY;
        }
        return DayOfWeek.SUNDAY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DayOfWeek getDatePersonal(int weekday) {
        if (weekday == 0) {
            return DayOfWeek.SUNDAY;
        } else if (weekday == 1) {
            return DayOfWeek.MONDAY;
        } else if (weekday == 2) {
            return DayOfWeek.TUESDAY;
        } else if (weekday == 3) {
            return DayOfWeek.WEDNESDAY;
        } else if (weekday == 4) {
            return DayOfWeek.THURSDAY;
        } else if (weekday == 5) {
            return DayOfWeek.FRIDAY;
        } else if (weekday == 6) {
            return DayOfWeek.SATURDAY;
        }
        return DayOfWeek.SUNDAY;
    }

    /*
    calculate the new schedule's time to put in the group timetable
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void putCalcul() {
        LocalDate nowDate = LocalDate.now();
        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.getResult().getValue().toString().equals("0")) {
                    Toast.makeText(getApplicationContext(), "?????? ?????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Schedule> forCalculating = new ArrayList<>();
                    forCalculating = timetable.getAllSchedulesInStickers();
                    Schedule[] baseSchedule = new Schedule[forCalculating.size()];
                    int i = 0;
                    for (Schedule s : forCalculating)
                        baseSchedule[i++] = s;
                    Schedule createTime = new Schedule();
                    if (calculate(baseSchedule, createTime, hourInt * 100 + minuteInt)) {
                        LocalDate calculDate;
                        calculDate = nowDate.with(TemporalAdjusters.nextOrSame(getDate(createTime.getDay())));
                        int startHour = createTime.getStartTime().getHour();
                        int startMinute = createTime.getStartTime().getMinute();
                        if (startHour == 0) {
                            startHour = 9;
                        }
                        int year = calculDate.getYear();
                        int month = calculDate.getMonthValue();
                        int day = calculDate.getDayOfMonth();
                        int endHour = startHour + hourInt;
                        int endMinute = startMinute + minuteInt;
                        Time endTimeTik = createTime.getStartTime();
                        int weekdayIndex = getWeekdayIndex(calculDate.getDayOfWeek().toString());
                        String calculDateStr = Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day);
                        addNew(weekdayIndex, name + "'s meeting", "", new Time(startHour, startMinute), new Time(endHour, endMinute));
                        groupRef.child(groupCode).child("GroupSchedule").child("schedule").setValue(weekdayIndex + "!/" + name + "'s meeting" + "!/" + startHour + ":" + startMinute + "!/" + endHour + ":" + endMinute + "!/" + calculDateStr);
                        planName = name + "'s meeting";
                        setColor(planName);
                        //String date, String startTime, String endTime, String title
                        addPlan(calculDateStr, startHour + ":" + startMinute, endHour + ":" + endMinute, name + "'s meeting");
                        if(!membersToken.isEmpty()){
                            for(String token:membersToken){
                                On_MakeNotification(token, name, "????????? ????????? ?????????????????????.", "TeamOne");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /*
    result from add new group schedule
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                weekdayTrue = data.getExtras().getBooleanArray("weekday");
                hourInt = data.getExtras().getInt("hour");
                minuteInt = data.getExtras().getInt("minute");
                putCalcul();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // remove data and create new group timetable
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reset() {
        members.removeAll(members);
        timetable.removeAll();

        LocalDate nowDate = LocalDate.now();
        LocalDate sunday = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate satday = nowDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        LocalDate nextDate = LocalDate.now().plusDays(6);


        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot member : task.getResult().getChildren()) {
                    planRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for (DataSnapshot plan : task.getResult().getChildren()) {
                                String a = plan.getKey();
                                planRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {

                                        String title = plan.child("title").getValue().toString();
                                        if (!title.equals(planName)) {
                                            String date = plan.child("date").getValue().toString();
                                            String time = plan.child("time").getValue().toString();
                                            String[] timeSplitPlan = time.split("~");
                                            int startHour = Integer.parseInt(timeSplitPlan[0].substring(0, 2));
                                            int startMinute = Integer.parseInt(timeSplitPlan[0].substring(3, 5));
                                            int endHour = Integer.parseInt(timeSplitPlan[1].substring(0, 2));
                                            int endMinute = Integer.parseInt(timeSplitPlan[1].substring(3, 5));

                                            String[] dateSplitPlan = date.split("/");
                                            LocalDate tmpDatePlan = LocalDate.of(Integer.parseInt(dateSplitPlan[0]), Integer.parseInt(dateSplitPlan[1]), Integer.parseInt(dateSplitPlan[2]));
                                            int weekday = getWeekdayIndex(tmpDatePlan.getDayOfWeek().toString());

                                            if (getDateDif(tmpDatePlan, nowDate) < 0) {
                                                deletePlan(title, dateSplitPlan[0] + dateSplitPlan[1] + dateSplitPlan[2], time,member.getKey());
                                            } else if (getDateDif(tmpDatePlan, nowDate) > 0 && getDateDif(tmpDatePlan, nextDate) < 0) {
                                                addNew(weekday, "", "", new Time(startHour, startMinute), new Time(endHour, endMinute));
                                                setColor(planName);
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });

                }

            }
        });

        groupRef.child(groupCode).child("GroupSchedule").child("schedule").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.getResult().getValue().toString().equals("0")) {
                    String s = task.getResult().getValue().toString();
                    String[] fortable = s.split("!/");
                    String[] starttime = fortable[2].split(":");
                    String[] Endtime = fortable[3].split(":");
                    String[] date = fortable[4].split("/");

                    LocalDate tmpDate = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

                    if (getDateDif(tmpDate, nowDate) < 0) {
                        deleteGroupPlan();
                    } else if (getDateDif(tmpDate, nowDate) > 0 && getDateDif(tmpDate, nextDate) < 0) {
                        addNew(Integer.parseInt(fortable[0]), fortable[1], "", new Time(Integer.parseInt(starttime[0]), Integer.parseInt(starttime[1])), new Time(Integer.parseInt(Endtime[0]), Integer.parseInt(Endtime[1])));
                        setColor(planName);
                    }
                    planName = fortable[1];
                }

            }
        });

        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot member : task.getResult().getChildren()) {
                    members.add(member.getKey());
                    scheduleRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            for (DataSnapshot schedules : task.getResult().getChildren()) {
                                String a = schedules.getKey();
                                scheduleRef.child(member.getKey()).child(a).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                        String title = task.getResult().child("title").getValue().toString();
                                        String weekday = task.getResult().child("weekday").getValue().toString();
                                        String Time = task.getResult().child("time").getValue().toString();
                                        String[] times = Time.split("~");
                                        String[] startTime = times[0].split(":");
                                        String[] endTime = times[1].split(":");
                                        String startDate = task.getResult().child("startDate").getValue().toString();
                                        String endDate = task.getResult().child("endDate").getValue().toString();
                                        String[] startDateSplit = startDate.split("/");
                                        String[] endDateSplit = endDate.split("/");

                                        LocalDate startLocalDate = LocalDate.of(Integer.parseInt(startDateSplit[0]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[2]));
                                        LocalDate endLocalDate = LocalDate.of(Integer.parseInt(endDateSplit[0]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[2]));
                                        LocalDate tmpDate = nowDate.with(TemporalAdjusters.nextOrSame(getDatePersonal(Integer.parseInt(weekday))));

                                        if (getDateDif(tmpDate, startLocalDate) >= 0 && getDateDif(tmpDate, endLocalDate) <= 0) {
                                            addNew(Integer.parseInt(weekday), "", "", new Time(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1])), new Time(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1])));
                                            setColor(planName);
                                        } else if (getDateDif(tmpDate, endLocalDate) > 0) {
                                            deleteSchedule(title, startDateSplit[0] + startDateSplit[1] + startDateSplit[2], endDateSplit[0] + endDateSplit[1] + endDateSplit[2], Time, weekday,member.getKey());
                                        }
                                    }

                                });
                            }
                        }
                    });

                }

            }
        });

    }

    public void addNew(int day, String title, String place, Time startTime, Time endTime) {
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

    /*
    check if there is any time to put in new group schedule into group timetable
    make each weekday's index and Schedul[e
    get each weekday's schedules or plans and count, and merge it
    if the weekday is selected, check if there is any time to put in new schedule
    return the result
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean calculate(Schedule[] groupSchedule, Schedule newSchedule, int length) {

        int index = groupSchedule.length;

        Schedule[][] day = new Schedule[7][groupSchedule.length];

        int[] indicies = new int[7];

        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < index; i++) {
                day[j][i] = new Schedule();
            }
        }

        for (int i = 0; i < index; i++) {
            if (groupSchedule[i].getDay() == 0) {
                day[0][indicies[0]] = groupSchedule[i];
                indicies[0]++;
            } else if (groupSchedule[i].getDay() == 1) {
                day[1][indicies[1]] = groupSchedule[i];
                indicies[1]++;
            } else if (groupSchedule[i].getDay() == 2) {
                day[2][indicies[2]] = groupSchedule[i];
                indicies[2]++;
            } else if (groupSchedule[i].getDay() == 3) {
                day[3][indicies[3]] = groupSchedule[i];
                indicies[3]++;
            } else if (groupSchedule[i].getDay() == 4) {
                day[4][indicies[4]] = groupSchedule[i];
                indicies[4]++;
            } else if (groupSchedule[i].getDay() == 5) {
                day[5][indicies[5]] = groupSchedule[i];
                indicies[5]++;
            } else if (groupSchedule[i].getDay() == 6) {
                day[6][indicies[6]] = groupSchedule[i];
                indicies[6]++;
            }
        }

        Schedule[][] merged = new Schedule[7][index];

        LocalDate nowDate = LocalDate.now();
        int weekday = getWeekdayIndex(nowDate.getDayOfWeek().toString());
        int i=weekday+1;
        if(i==7){
            i=0;
        }
        boolean correct=true;
        while(correct){
            for (int j = 0; j < index; j++) {
                merged[i][j] = new Schedule();
            }
            if (weekdayTrue[i] != false) {
                if (indicies[i] != 0) {
                    if (Merging(day[i], merged[i], newSchedule, indicies[i], length, i)) {
                        return true;
                    }
                } else if (indicies[i] == 0) {
                    Merging(day[i], merged[i], newSchedule, indicies[i], length, i);
                    return true;
                }
            }
            i+=1;
            if(i==7){
                i=0;
            }
            if(i==weekday){
                correct = false;
            }
        }
        return false;
    }

    public boolean Merging(Schedule[] days, Schedule[] merging, Schedule newschedule, int index, int length, int day) {

        Schedule temp = new Schedule();
        int done = 0;

        int minIndex = 0;
        int minStartTime = 2400; //???????????? ????????? 24??? ?????? ??? ??? & ??????????????? hour * 100 + minute??? ??????.

        for (int i = 0; i < index - 1; i++) {
            minIndex = i;
            for (int j = i; j < index; j++) {
                if (days[j].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute() < days[minIndex].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()) {
                    minIndex = j;
                }

                temp = days[minIndex];
                days[minIndex] = days[i];
                days[i] = temp;

            }
        } //sorting


        // need to merging them  -> We don't need to show name and title in this table. So only use time
        int count = 0; //merging ??? ?????? ??????
        // done??? merge??? ????????? array??? ??????
        // done = 0?????? ??????,

        while (done != index) {
            merging[count].setStartTime(days[done].getStartTime());
            merging[count].setEndTime(days[done].getEndTime());


            for (int i = done; i < index; i++) {
                if (merging[count].getStartTime().getHour() * 100 + merging[count].getStartTime().getMinute() <= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()// merging??? ??????????????? ??? ?????????
                        && merging[count].getEndTime().getHour() * 100 + merging[count].getEndTime().getMinute() >= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()  // merging??? ????????? ??? ????????? ????????? ???????????? ??????????????? ??????
                        && i != done
                ) {

                    if (merging[count].getEndTime().getHour() * 100 + merging[count].getEndTime().getMinute() < days[i].getEndTime().getHour() * 100 + days[i].getEndTime().getMinute()) {//merging??? ????????? ?????? ????????? ???????????? ?????? ??? ?????????
                        merging[count].setEndTime(days[i].getEndTime()); //??? ?????? ???????????? merging??? ????????? ???????????? ????????????
                        done++;//????????? ??????????????? ?????????
                        if (done == index)
                            break;
                    } else {
                        done++;
                        if (done == index)
                            break;
                    }
                }


            }
            if (done == index)
                break;
            done++;
            count++; //????????? ??? ?????? ?????? ?????? array??? value?????? ?????????????????? ???????????? ????????????.
        }

        return available(merging, newschedule, count, length, day - 1);

    }//merging function end


    //?????? ?????? ???????????? boolean????????? ???????????? ????????????
    //length ?????? ?????? ???????????? merge?????? ??? ??? ????????? ???????????? ????????? ????????? ??????????????? ????????? length?????? ?????? ??? ????????? ?????? ??? ??????.
    //?????? ????????? true??? ???????????? ?????? ?????? ???
    public boolean available(Schedule[] days, Schedule sample, int index, int length, int day) {


        sample.setDay(day);

        if (index == 1) { // ????????? ????????? merging??? ?????? ???????????? - 9??? or 10??? - ???????????? ?????? ?????? length ????????? return true

            if (days[0].getStartTime().getHour() * 100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9, 0));
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            } else if (2400 - days[0].getEndTime().getHour() * 100 + days[0].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[0].getEndTime());
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            } else return false;
        } else if (index > 1) {   // index??? 2??? ???????????? index 0 ??? ???????????? - 9??? ?????? length ???????????? return true ?????? ??????  (index i+1 ?????? ??????) - (index i ?????????) ??? ?????? return true, ????????? 10???(default ????????? ??????) - (index i ?????????) ?????? return true


            if (days[0].getStartTime().getHour() * 100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9, 0));
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            }

            for (int i = 0; i < index - 1; i++) {


                int hour = days[i + 1].getStartTime().getHour() - days[i].getEndTime().getHour();
                int minute = days[i + 1].getStartTime().getMinute() - days[i].getEndTime().getMinute();

                if (minute < 0) {
                    hour--;
                    minute = 60 + minute;
                }//60????????? ????????? ?????? ????????? ?????? ???????????????


                if (hour * 100 + minute >= length) {

                    sample.setStartTime(new Time(days[i].getEndTime().getHour(), days[i].getEndTime().getMinute()));

                    int endMinute = days[i].getEndTime().getMinute() + length % 100;
                    int endHour = days[i].getEndTime().getHour() + length / 100;
                    if (endMinute >= 60) {
                        endHour++;
                        endMinute = endMinute - 60;
                    }

                    sample.setEndTime(new Time(endHour, endMinute));

                    return true;

                }
            }

            if (2400 - days[index - 1].getEndTime().getHour() * 100 + days[index - 1].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[index - 1].getEndTime());
                int hour = length / 100;
                int minute = length % 100;
                sample.setEndTime(new Time(sample.getStartTime().getHour() + hour, sample.getStartTime().getMinute() + minute));
                return true;
            }

        }
        return false;
    }


    /*
    if the planName is not set, change schedule which has same name of planName.
     */
    public void setColor(String planName) {
        ArrayList<String> a = new ArrayList<String>();
        a.add(planName);
        timetable.setTableColor();
        if (!planName.equals("not yet !@!@#@$")) {
            timetable.setGroupPlanColor(a);
        }
    }

    public int nextWeekday(int now) {
        if (now < 6) {
            return now + 1;
        } else {
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getDateDif(LocalDate dgf, LocalDate target) {
        return dgf.compareTo(target);
    }

    private void deleteGroupPlan() {
        groupRef.child(groupCode).child("GroupSchedule").child("schedule").setValue("0");
    }

    private void deletePlan(String title, String date, String time,String ID) {
        planRef.child(ID).child(date + "_" + time + "_" + title).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(Void unused) {
                reset();
            }
        });
    }

    private void deleteSchedule(String title, String startDate, String endDate, String time, String weekdayIndex,String ID) {
        scheduleRef.child(ID).child(startDate + "~" + endDate + "_" + time + "_" + title + "_" + weekdayIndex).setValue(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPlan(String date, String startTime, String endTime, String title) {

        startTime = convertPlanTime(startTime);
        endTime = convertPlanTime(endTime);

        String finalDate[] = convertPlanDate(date);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        groupRef.child(groupCode).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot member : task.getResult().getChildren()) {
                    planRef.child(member.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            planRef.child(member.getKey()).child(finalDate[0] + finalDate[1] + finalDate[2] + "_" + finalStartTime + "~" + finalEndTime + "_" + title).child("title").setValue(title);
                            planRef.child(member.getKey()).child(finalDate[0] + finalDate[1] + finalDate[2] + "_" + finalStartTime + "~" + finalEndTime + "_" + title).child("date").setValue(finalDate[0] + "/" + finalDate[1] + "/" + finalDate[2]);
                            planRef.child(member.getKey()).child(finalDate[0] + finalDate[1] + finalDate[2] + "_" + finalStartTime + "~" + finalEndTime + "_" + title).child("time").setValue(finalStartTime + "~" + finalEndTime);
                        }
                    });
                }
            }
        });
    }

    public String[] convertPlanDate(String date) {
        String[] dateSplit = date.split("/");
        if (Integer.parseInt(dateSplit[0]) < 10) {
            dateSplit[0] = "0" + dateSplit[0];
        }
        if (Integer.parseInt(dateSplit[1]) < 10) {
            dateSplit[1] = "0" + dateSplit[1];
        }
        if (Integer.parseInt(dateSplit[2]) < 10) {
            dateSplit[2] = "0" + dateSplit[2];
        }
        return dateSplit;


    }

    public String convertPlanTime(String time) {
        String[] timeSplit = time.split(":");
        if (Integer.parseInt(timeSplit[0]) < 10) {
            timeSplit[0] = "0" + timeSplit[0];
        }
        if (Integer.parseInt(timeSplit[1]) < 10) {
            timeSplit[1] = "0" + timeSplit[1];
        }
        return timeSplit[0] + ":" + timeSplit[1];
    }

    public int getWeekdayIndex(String weekday) {
        if (weekday.equals("SUNDAY")) {
            return 0;
        } else if (weekday.equals("MONDAY")) {
            return 1;
        } else if (weekday.equals("TUESDAY")) {
            return 2;
        } else if (weekday.equals("WEDNESDAY")) {
            return 3;
        } else if (weekday.equals("THURSDAY")) {
            return 4;
        } else if (weekday.equals("FRIDAY")) {
            return 5;
        } else if (weekday.equals("SATURDAY")) {
            return 6;
        } else {
            return 0;
        }
    }

    public int getHeaderIndex(String weekday) {
        if (weekday.equals("SUNDAY")) {
            return 1;
        } else if (weekday.equals("MONDAY")) {
            return 2;
        } else if (weekday.equals("TUESDAY")) {
            return 3;
        } else if (weekday.equals("WEDNESDAY")) {
            return 4;
        } else if (weekday.equals("THURSDAY")) {
            return 5;
        } else if (weekday.equals("FRIDAY")) {
            return 6;
        } else if (weekday.equals("SATURDAY")) {
            return 7;
        } else {
            return 0;
        }
    }
    public Task<String> sendFCM(String regToken, String title, String message, String SubTitle) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", regToken);
        data.put("text", message);
        data.put("title", title); // ?????????
        data.put("subtext", SubTitle);
        data.put("android_channel_id", "Group");

        return mFunctions
                .getHttpsCallable("sendFCM")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) Objects.requireNonNull(task.getResult()).getData().toString();
                        Log.d("SendPush", "then: " + result);
                        return result;
                    }
                });
    }

    private void On_MakeNotification(String token, String Title, String text, String SubTitle) {
        sendFCM(token, Title, text, SubTitle)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();

                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            Log.w("SendPush", "makeNotification:onFailure", e);
                            return;
                        }

                        String result = task.getResult();

                    }
                });

    }
}
