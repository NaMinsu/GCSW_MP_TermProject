package com.example.teamone;

import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamone.todayScheduleData;
import com.example.teamone.R;

import java.util.ArrayList;

public class todaySchedule extends RecyclerView.Adapter<todaySchedule.ItemViewHolder> {

    /*
    메인 fragment에서
     */
    private ArrayList<todayScheduleData> listData = new ArrayList<todayScheduleData>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todayschedule, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void remove(){listData.clear();}
    public void addItem(todayScheduleData data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private TextView textView3;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.scheduleTitle);
            textView2 = itemView.findViewById(R.id.scheduleContent);
            textView3 = itemView.findViewById(R.id.scheduleTime);


        }

        void onBind(todayScheduleData data) {
            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
            textView3.setText(data.getTime());
        }
    }
}