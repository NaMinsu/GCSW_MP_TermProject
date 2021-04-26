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

public class friendAdapter extends RecyclerView.Adapter<FriendViewHolder>{
    Context mContext;
    ArrayList<String> dataList;

    friendAdapter(Context c, ArrayList<String> list) {
        mContext = c;
        dataList = list;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_friend, parent, false);
        FriendViewHolder cvh = new FriendViewHolder(view, mContext);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
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

class FriendViewHolder extends RecyclerView.ViewHolder {
    public TextView txt_name;
    public TextView txt_email;
    Context ctx;

    public FriendViewHolder(@NonNull View itemView, Context c) {
        super(itemView);
        ctx = c;
        txt_name = itemView.findViewById(R.id.friendName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(ctx, friendInfo.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            }
        });
    }

    public void onBind(String dataTxt) {
        txt_name.setText(dataTxt);
    }
}