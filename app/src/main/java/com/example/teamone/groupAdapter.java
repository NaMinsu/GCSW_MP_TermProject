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

public class groupAdapter extends RecyclerView.Adapter<GroupViewHolder>{
    Context mContext;
    ArrayList<String> dataList;

    // constructor of adapter
    groupAdapter(Context c, ArrayList<String> list) {
        mContext = c; // context of adapted activity
        dataList = list; // list of group to show
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext(); // get context
        // inflation
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // setup recyclerview item
        View view = inflater.inflate(R.layout.items_group, parent, false);
        GroupViewHolder cvh = new GroupViewHolder(view, mContext);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        // show data in view
        holder.onBind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        // counting items only list is not null
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }
}

// Viewholder of adapter
class GroupViewHolder extends RecyclerView.ViewHolder {
    public TextView txtView; // group name textview
    public TextView CodeView; // group code textview
    Context ctx;

    // constructor of viewholder
    public GroupViewHolder(@NonNull View itemView, Context c) {
        super(itemView);
        ctx = c;
        txtView = itemView.findViewById(R.id.groupName);
        CodeView = itemView.findViewById(R.id.GroupCode);

        // put OnClickListener to process click event
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a group is clicked, redirect group table page
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(ctx, groupTable.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // send group name and code to get group data from database
                    intent.putExtra("code", CodeView.getText().toString());
                    intent.putExtra("name", txtView.getText().toString());
                    ctx.startActivity(intent);
                }
            }
        });
    }

    // the function to show group name
    public void onBind(String data) {
    String[] Data = data.split("@Admin_split@");
        CodeView.setText(Data[0]);
        txtView.setText(Data[1]);
    }
}
