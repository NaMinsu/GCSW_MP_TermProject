package com.gcsw.teamone;

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

public class deletePlanAdapter extends RecyclerView.Adapter<deletePlanAdapter.ItemViewHolder> {

    public ArrayList<todayScheduleData> listData = new ArrayList<todayScheduleData>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference planRef = database.getReference("plan");

    @NonNull
    @Override
    public deletePlanAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_delete_plan, parent, false);
        return new deletePlanAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull deletePlanAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void remove() {
        listData.clear();
    }

    public void addItem(todayScheduleData data) {
        listData.add(data);
    }


    /*
    리사이클러 뷰 내부에서 사용될 ItemViewHolder입니다.
    해당 ItemViewHolder에 있는 버튼을 클릭시
    리스트에서 제거하고 데이터베이스에서 삭제합니다.
    또한 deletePlan 클래스의 findNoPlan 함수를 실행합니다.
     */
    /*
    ItemViewHolder to be used inside the Recycler View.
    Click the button on the itemViewHolder.
    Remove from list and delete from database.
    It also executes the findNoPlan function of the deletePlan class.
     */
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private todayScheduleData data;

        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private Button deleteBtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.deletePlanTitle);
            textView2 = itemView.findViewById(R.id.deletePlanDate);
            textView3 = itemView.findViewById(R.id.deletePlanTime);
            deleteBtn = itemView.findViewById(R.id.deletePlanGoBtn);

            textView1.setSelected(true);
            textView2.setSelected(true);
            textView3.setSelected(true);
        }

        void onBind(todayScheduleData data) {
            this.data = data;

            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
            textView3.setText(data.getTime());

            deleteBtn.setOnClickListener(this);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deletePlanGoBtn:
                    String[] date = textView2.getText().toString().split("/");
                    deletePlan(textView1.getText().toString(), date[0] + date[1] + date[2], textView3.getText().toString());
                    listData.remove(data);
                    notifyDataSetChanged();
                    ((deletePlan) deletePlan.contextPlan).findNoPlan();
            }
        }

        private void deletePlan(String title, String date, String time) {
            planRef.child(FirstAuthActivity.getMyID()).child(date + "_" + time + "_" + title).setValue(null);
        }
    }
}
