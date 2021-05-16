package com.example.teamone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
//67번째 줄에 그룹name도 같이 넘기도록 수정했습니다.
public class groupAdapter extends RecyclerView.Adapter<GroupViewHolder>{
    Context mContext;
    ArrayList<String> dataList;

    groupAdapter(Context c, ArrayList<String> list) {
        mContext = c;
        dataList = list;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_group, parent, false);
        GroupViewHolder cvh = new GroupViewHolder(view, mContext);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.onBind((String)dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder {
    public TextView txtView;
    Context ctx;

    public GroupViewHolder(@NonNull View itemView, Context c) {
        super(itemView);
        ctx = c;
        txtView = itemView.findViewById(R.id.groupName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(ctx, groupTable.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    HashMap<String, ArrayList<String>> groupdata = FragmentGroupList.getGroupMap();
                    ArrayList<String> members = groupdata.get(txtView.getText().toString());
                    intent.putExtra("name",txtView.getText().toString());
                    intent.putExtra("members", members);
                    ctx.startActivity(intent);
                }
            }
        });
    }

    public void onBind(String dataTxt) {
        txtView.setText(dataTxt);
    }
}
