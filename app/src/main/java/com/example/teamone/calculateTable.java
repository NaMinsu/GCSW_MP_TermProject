package com.example.teamone;

import com.github.tlaabs.timetableview.Schedule;

public class calculateTable {
//기본적으로 group timetable의 background를 가능한 시간에 채울 색으로 선언하기.
//그 후 시간표 값들을 읽어오면서 채워지는 시간의 background를 바꾸기
//가능한 시간 선택: 우리가 설정한 시간 조건에 맞춰서 schedule의 이름은 group@@'s meeting 으로, 나머지는 빈 값을 가진 상태로 넘어오기
    //length 는 int 값으로 시간, 분 받아서 시간 * 100  후에 시간 + 분 으로 계산해서 받기.
    public void calculate(Schedule[] groupSchedule, Schedule newSchedule, int lenght){

        int index = groupSchedule.length;

        Schedule[] mon = new Schedule[groupSchedule.length];
        Schedule[] tue = new Schedule[groupSchedule.length];
        Schedule[] wed = new Schedule[groupSchedule.length];
        Schedule[] thu = new Schedule[groupSchedule.length];
        Schedule[] fri = new Schedule[groupSchedule.length];
        Schedule[] sat = new Schedule[groupSchedule.length];
        Schedule[] sun = new Schedule[groupSchedule.length];

        int monIndex= 0;
        int tueIndex=0;
        int wedIndex=0;
        int thuIndex=0;
        int friIndex=0;
        int satIndex=0;
        int sunIndex=0;

        for(int i = 0;i < index; i++){
            mon[i] = null;
            tue[i] = null;
            wed[i] = null;
            thu[i] = null;
            fri[i] = null;
            sat[i] = null;
            sun[i] = null;
        }

        for(int i = 0;i < index; i++){
            if(groupSchedule[i].getDay() == 0){
                mon[monIndex] = groupSchedule[i];
                monIndex++;
            }

            else if(groupSchedule[i].getDay() ==1){
                tue[tueIndex] = groupSchedule[i];
                tueIndex++;
            }


            else if(groupSchedule[i].getDay() ==2){
                wed[wedIndex] = groupSchedule[i];
                wedIndex++;
            }

            else if(groupSchedule[i].getDay() ==3){
                thu[thuIndex] = groupSchedule[i];
                thuIndex++;
            }

            else if(groupSchedule[i].getDay() ==4){
                fri[friIndex] = groupSchedule[i];
                friIndex++;
            }

            else if(groupSchedule[i].getDay() ==5){
                sat[satIndex] = groupSchedule[i];
                satIndex++;
            }

            else if(groupSchedule[i].getDay() ==6){
                sun[sunIndex] = groupSchedule[i];
                sunIndex++;
            }
        }



    }

    public boolean checkAvailable(Schedule[] days, Schedule sample,int length){


        return true;
    }
}
