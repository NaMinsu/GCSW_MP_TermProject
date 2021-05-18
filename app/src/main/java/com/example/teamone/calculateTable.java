package com.example.teamone;


public class calculateTable {
    //기본적으로 group timetable의 background를 가능한 시간에 채울 색으로 선언하기.
//그 후 시간표 값들을 읽어오면서 채워지는 시간의 background를 바꾸기
//가능한 시간 선택: 우리가 설정한 시간 조건에 맞춰서 schedule의 이름은 group@@'s meeting 으로, 나머지는 빈 값을 가진 상태로 넘어오기
    //length 는 int 값으로 시간, 분 받아서 시간 * 100  후에 시간 + 분 으로 계산해서 받기.


    public static void calculate(Schedule[] groupSchedule,String code) {

        int index = groupSchedule.length;

        Schedule[][] day = new Schedule[7][groupSchedule.length];

        int[] indicies = new int[7];

        for (int j = 0; j < 7; j++){
            for (int i = 0; i < index; i++) {
                day[j][i] = new Schedule();
            }
        }

        //요일 나누기
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
        //나눈 요일별로 merging function의 결과를 담을 class
        Schedule[][] merged = new Schedule[7][index];

        //전체 초기화 및 요일별로 함수 돌리기
        for (int i = 0; i < 7; i++) {

            for(int j=0;j<index;j++){//요일 전체 초기화
                merged[i][j] = new Schedule();
            }
            if (indicies[i] != 0) {
                System.out.println(i);
                //요일별로 함수 실행, 그리고 여기서 available이 true가 나오면 true return하기
                Merging(day[i], merged[i],indicies[i],code);

            }
        }
    }


    public static boolean input(Schedule[] personal,Schedule newone){

        int index = personal.length;

        Schedule[][] day = new Schedule[7][index];

        int[] indicies = new int[7];

        for (int j = 0; j < 7; j++){
            for (int i = 0; i < index; i++) {
                day[j][i] = new Schedule();
            }
        }

        //요일 나누기
        for (int i = 0; i < index; i++) {
            if (personal[i].getDay() == 0) {
                day[0][indicies[0]] = personal[i];
                indicies[0]++;

            } else if (personal[i].getDay() == 1) {
                day[1][indicies[1]] = personal[i];
                indicies[1]++;
            } else if (personal[i].getDay() == 2) {
                day[2][indicies[2]] = personal[i];
                indicies[2]++;
            } else if (personal[i].getDay() == 3) {
                day[3][indicies[3]] = personal[i];
                indicies[3]++;
            } else if (personal[i].getDay() == 4) {
                day[4][indicies[4]] = personal[i];
                indicies[4]++;
            } else if (personal[i].getDay() == 5) {
                day[5][indicies[5]] = personal[i];
                indicies[5]++;
            } else if (personal[i].getDay() == 6) {
                day[6][indicies[6]] = personal[i];
                indicies[6]++;
            }
        }

        for(int i = 0; i < 6; i++){
            if(newone.getDay() == i){
                for(int j = 0; j < indicies[i];j++){
                    // 끝나는 시간이 원래 시작시간과 끝 시간 사이일때 false (insert 불가)
                    if(newone.getEndTime().getHour()*100 + newone.getEndTime().getMinute() >= day[i][j].getStartTime().getHour()*100 + day[i][j].getStartTime().getMinute()
                    && newone.getEndTime().getHour()*100 + newone.getEndTime().getMinute() <= day[i][j].getEndTime().getHour()*100 + day[i][j].getEndTime().getMinute()
                    )
                        return false;

                    //시작시간의 기존의 시간표의 시작시간과 끝 시간 사이에 있을때 false ( insert 불가)
                    else if(newone.getStartTime().getHour()*100 + newone.getStartTime().getMinute() >= day[i][j].getStartTime().getHour()*100 + day[i][j].getStartTime().getMinute()
                            && newone.getStartTime().getHour()*100 + newone.getStartTime().getMinute() <= day[i][j].getEndTime().getHour()*100 + day[i][j].getEndTime().getMinute())
                        return false;
                }
            }
        }

        return true; // true가 오면 TimeTableView의 addnew 사용해서 insert
    }


    //요일별 시간표를 한개로 합치기 -> 예를들어 월요일 9시부터 1시 수업, 다른 그룹멤버의 사간표가 12시부터 2시까지 수업이면 이 일정들을 합해 9시 ~ 2시 로 만들기.
    //merging 함수에서 합치기
    public static void Merging(Schedule[] days,Schedule[] merging, int index,String code) {

        Schedule temp = new Schedule();
        int done=0;

        int minIndex= 0;
        int minStartTime=2400; //시간이기 때문에 24가 가장 큰 수 & 시간계산은 hour * 100 + minute로 할것.

        for(int i = 0;i<index-1;i++){
            minIndex = i;
            for(int j = i; j < index; j++){
                if(days[j].getStartTime().getHour()*100 + days[i].getStartTime().getMinute() < days[minIndex].getStartTime().getHour()*100 + days[i].getStartTime().getMinute()) {
                    minIndex = j;
                }

                temp = days[minIndex];
                days[minIndex] = days[i];
                days[i] = temp;

            }
        } //sorting



        // need to merging them  -> We don't need to show name and title in this table. So only use time
        int count =0; //merging 의 갯수 새기
        // done는 merge에 포함된 array의 갯수
        // done = 0부터 시작,

        while(done != index) {
            merging[count].setStartTime(days[done].getStartTime());
            merging[count].setEndTime(days[done].getEndTime());


            for (int i = done; i < index; i++) {
                if (merging[count].getStartTime().getHour() * 100 + merging[count].getStartTime().getMinute() <= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()// merging의 시작시간이 더 빠르고
                        && merging[count].getEndTime().getHour()*100 + merging[count].getEndTime().getMinute() >= days[i].getStartTime().getHour() * 100 + days[i].getStartTime().getMinute()  // merging의 시작과 끝 사이에 새로운 스케쥴의 시작시간이 있고
                        &&i != done
                ) {

                    if(merging[count].getEndTime().getHour()*100 + merging[count].getEndTime().getMinute() <days[i].getEndTime().getHour() * 100 + days[i].getEndTime().getMinute()) {//merging의 끝시간 보다 새로운 스케쥴의 끝이 더 느릴때
                        merging[count].setEndTime(days[i].getEndTime()); //더 늦게 끝나는걸 merging의 끝나는 시간으로 선택하기
                        done++;//몇개가 합쳐졌는지 카운트
                        if (done == index)
                            break;
                    }

                    else {
                        done++;
                        if (done == index)
                            break;
                    }
                }



            }
            if (done == index)
                break;
            done++;
            count++; //한바퀴 다 돌고 아직 모든 array의 value들이 안합쳐졌다면 나머지도 합쳐야함.
        }

        int size = merging.length;
        for(int i =0;i<size;i++){

        }

    }//merging function end





    //합친 것을 바탕으로 boolean함수로 가능한지 확인하기
    //length 받은 것을 바탕으로 merge완료 된 각 날짜의 스케줄의 끝나는 시간과 시작시간을 빼면서 length보다 작은 값 나오는 것들 다 찾기.
    //만약 여기서 true가 나온다면 전체 함수 끝
    public static boolean available(Schedule[] days, Schedule sample, int index,int length){


        if(index == 1) { // 한개로 전체가 merging된 경우 시작시간 - 9시 or 10시 - 끝난시간 해서 이게 length 안이면 return true

            if(days[0].getStartTime().getHour()*100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9,0));
                int hour = length/100;
                int minute = length%100;
                sample.setEndTime(new Time(sample.getStartTime().getHour()+hour,sample.getStartTime().getMinute()+minute));
                return true;
            }

            else if(2200 - days[0].getEndTime().getHour()*100 + days[0].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[0].getEndTime());
                int hour = length/100;
                int minute = length%100;
                sample.setEndTime(new Time(sample.getStartTime().getHour()+hour,sample.getStartTime().getMinute()+minute));
                return true;
            }

            else return false;
        }

        else if(index > 1){   // index가 2개 이상이면 index 0 의 시작시간 - 9시 해서 length 안쪽이면 return true 아닐 경우  (index i+1 시작 시간) - (index i 끝시간) 일 경우 return true, 아니면 10시(default 마지막 시간) - (index i 끝시간) 이면 return true


            if(days[0].getStartTime().getHour()*100 + days[0].getStartTime().getMinute() - 900 >= length) {
                sample.setStartTime(new Time(9,0));
                int hour = length/100;
                int minute = length%100;
                sample.setEndTime(new Time(sample.getStartTime().getHour()+hour,sample.getStartTime().getMinute()+minute));
                return true;
            }

            for(int i =0 ; i < index -1 ; i++) {


                int hour = days[i+1].getStartTime().getHour() - days[i].getEndTime().getHour();
                int minute = days[i+1].getStartTime().getMinute() - days[i].getEndTime().getMinute();

                if(minute < 0) {
                    hour--;
                    minute = 60 + minute;
                }//60분이기 때문에 그에 맞춰서 다시 계산해주기


                if(hour * 100 + minute >= length) {

                    sample.setStartTime(new Time(days[i].getEndTime().getHour(),days[i].getEndTime().getMinute()));

                    int endMinute = days[i].getEndTime().getMinute() + length%100;
                    int endHour = days[i].getEndTime().getHour() + length/100;
                    if(endMinute >= 60) {
                        endHour++;
                        endMinute = endMinute-60;
                    }

                    sample.setEndTime(new Time(endHour,endMinute));

                    return true;

                }
            }

            if(2200 - days[index-1].getEndTime().getHour()*100 + days[index-1].getEndTime().getMinute() >= length) {
                sample.setStartTime(days[index-1].getEndTime());
                int hour = length/100;
                int minute = length%100;
                sample.setEndTime(new Time(sample.getStartTime().getHour()+hour,sample.getStartTime().getMinute()+minute));
                return true;
            }

        }

        else
            return false; //이 요일에 schedule가 아예 없는 경우

        return false; //위 경우 모두 아니면 return false
    }

}