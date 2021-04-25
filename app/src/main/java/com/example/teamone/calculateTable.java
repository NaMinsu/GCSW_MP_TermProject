package com.example.teamone;

import com.github.tlaabs.timetableview.Schedule;

public class calculateTable {
    //기본적으로 group timetable의 background를 가능한 시간에 채울 색으로 선언하기.
//그 후 시간표 값들을 읽어오면서 채워지는 시간의 background를 바꾸기
//가능한 시간 선택: 우리가 설정한 시간 조건에 맞춰서 schedule의 이름은 group@@'s meeting 으로, 나머지는 빈 값을 가진 상태로 넘어오기
    //length 는 int 값으로 시간, 분 받아서 시간 * 100  후에 시간 + 분 으로 계산해서 받기.
    public void calculate(Schedule[] groupSchedule, Schedule newSchedule, int length) {

        int index = groupSchedule.length;

        Schedule[][] day = new Schedule[7][groupSchedule.length];

        int monIndex = 0;
        int tueIndex = 0;
        int wedIndex = 0;
        int thuIndex = 0;
        int friIndex = 0;
        int satIndex = 0;
        int sunIndex = 0;

        for (int j = 0; j < 7; j++){
            for (int i = 0; i < index; i++) {
                day[j][i] = new Schedule();
            }
    }
        for (int i = 0; i < index; i++) {
            if (groupSchedule[i].getDay() == 0) {
                day[0][monIndex] = groupSchedule[i];
                monIndex++;
            } else if (groupSchedule[i].getDay() == 1) {
                day[1][tueIndex] = groupSchedule[i];
                tueIndex++;
            } else if (groupSchedule[i].getDay() == 2) {
                day[2][wedIndex] = groupSchedule[i];
                wedIndex++;
            } else if (groupSchedule[i].getDay() == 3) {
                day[3][thuIndex] = groupSchedule[i];
                thuIndex++;
            } else if (groupSchedule[i].getDay() == 4) {
                day[4][friIndex] = groupSchedule[i];
                friIndex++;
            } else if (groupSchedule[i].getDay() == 5) {
                day[5][satIndex] = groupSchedule[i];
                satIndex++;
            } else if (groupSchedule[i].getDay() == 6) {
                day[6][sunIndex] = groupSchedule[i];
                sunIndex++;
            }
        }
        Schedule[][] merged = new Schedule[7][index];

        for (int i = 0; i < 7; i++) {
            for(int j=0;j<index;j++){
                merged[i][j] = new Schedule();
            }
            Merging(day[i],merged[i]);
            if(available(merged[i],newSchedule,length)){

            }
        }


    }


    // 요일별 시간표를 한개로 합치기 -> 예를들어 월요일 9시부터 1시 수업, 다른 그룹멤버의 사간표가 12시부터 2시까지 수업이면 이 일정들을 합해 9시 ~ 2시 로 만들기.
   //merging 함수에서 합치기
    public void Merging(Schedule[] days,Schedule[] merging) {
        int index = days.length;

        Schedule temp = new Schedule();

        int minIndex= 0;
        int minStartTime=2400; //시간이기 때문에 24가 가장 큰 수 & 시간계산은 hour * 100 + minute로 할것.
        for(int i = 0;i<index;i++){
            if(days[i].getStartTime().getHour()*100 + days[i].getStartTime().getMinute() < minStartTime) {
                minStartTime = days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute();
                minIndex = i;
            }
        } // 가장 작은값 찾기. 이걸 기준으로 start Time이 이 스케쥴의 사이에 있는것들을 모두 병합해서 하나로 만들기



    }


    //합친 것을 바탕으로 boolean함수로 가능한지 확인하기
    //length 받은 것을 바탕으로 merge완료 된 각 날짜의 스케줄의 끝나는 시간과 시작시간을 빼면서 length보다 작은 값 나오는 것들 다 찾기.
    public boolean available(Schedule[] days, Schedule sample, int length){

        return true;
    }

}

