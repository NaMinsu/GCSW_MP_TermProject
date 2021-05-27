package com.example.teamone;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class deleteScheduleAdapter extends RecyclerView.Adapter<deleteScheduleAdapter.ItemViewHolder>{

    private ArrayList<todayScheduleDataItem> listData = new ArrayList<todayScheduleDataItem>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference scheduleRef = database.getReference("schedule");

    @NonNull
    @Override
    public deleteScheduleAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_delete_schedule, parent, false);
        return new deleteScheduleAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull deleteScheduleAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void remove(){listData.clear();}

    public void addItem(todayScheduleDataItem data) {
        listData.add(data);
    }

    /*
    리사이클러 뷰 내부에서 사용될 ItemViewHolder입니다.
    해당 ItemViewHolder에 있는 버튼을 클릭시
    리스트에서 제거하고 데이터베이스에서 삭제합니다.
    또한 deleteSchedule 클래스의 findNoSchedule 함수를 실행합니다.
     */
    /*
    ItemViewHolder to be used inside the Recycler View.
    Click the button on the itemViewHolder.
    Remove from list and delete from database.
    It also executes the findNoSchedule function of the deleteSchedule class.
    */
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private todayScheduleDataItem data;

        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private Button deleteBtn;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.deleteScheduleTitle);
            textView2 = itemView.findViewById(R.id.deleteScheduleDate);
            textView3 = itemView.findViewById(R.id.deleteScheduleTime);
            textView4 = itemView.findViewById(R.id.deleteScheduleWeekday);
            deleteBtn = itemView.findViewById(R.id.deleteScheduleGoBtn);

            textView1.setSelected(true);
            textView2.setSelected(true);
            textView3.setSelected(true);
            textView4.setSelected(true);
        }

        void onBind(todayScheduleDataItem data) {
            this.data = data;

            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
            textView3.setText(data.getTime());
            textView4.setText(data.getWeekday());

            deleteBtn.setOnClickListener(this);

        }

        public void onClick(View v){
            switch(v.getId()){
                case R.id.deleteScheduleGoBtn:
                    String[] date = textView2.getText().toString().split("~");
                    String[] startDate = date[0].split("/");
                    String[] endDate = date[1].split("/");
                    String aimDate = startDate[0]+startDate[1]+startDate[2]+"~"+endDate[0]+endDate[1]+endDate[2];
                    aimDate = aimDate.replace(" ","");
                    deleteSchedule(textView1.getText().toString(),aimDate,textView3.getText().toString(),textView4.getText().toString());
                    listData.remove(data);
                    notifyDataSetChanged();
                    ((deleteSchedule)deleteSchedule.contextSchedule).findNoSchedule();
            }
        }

        private void deleteSchedule(String title, String date, String time,String weekday){
            scheduleRef.child(FirstAuthActivity.getMyID()).child(date+"_"+time+"_"+title+"_"+getWeekdayIndex(weekday)).setValue(null);
        }

        private int getWeekdayIndex(String weekday){
            if(weekday.equals("일요일")){
                return 0;
            }else if(weekday.equals("월요일")){
                return 1;
            }else if(weekday.equals("화요일")){
                return 2;
            }else if(weekday.equals("수요일")){
                return 3;
            }else if(weekday.equals("목요일")){
                return 4;
            }else if(weekday.equals("금요일")){
                return 5;
            }else if(weekday.equals("토요일")){
                return 6;
            }else{
                return 0;
            }
        }
    }
}

