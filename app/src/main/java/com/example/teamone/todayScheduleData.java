package com.example.teamone;
public class todayScheduleData {

    /*
     * plan을 저장하기 위한 클래스입니다.
     * todayScheduleData는 plan을 저장하기 위한 클래스인데
     * 계획 초기에 plan을 today schedule로 구상했었기 때문에 명칭이 겹쳐 이렇게 이름을 지었습니다.
     */

    private String title;
    private String content;
    private String time;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }


}