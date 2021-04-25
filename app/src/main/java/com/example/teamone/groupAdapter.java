package com.example.teamone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class groupAdapter extends RecyclerView.Adapter<CustomViewHolder>{
    ArrayList<String> dataList;

    groupAdapter(ArrayList<String> list) {
        dataList = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View)inflater.inflate(R.layout.items_group, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (dataList == null)
            dataList.add("Dummy");
        holder.txtView.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }
}

class CustomViewHolder extends RecyclerView.ViewHolder {
    public TextView txtView;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        txtView = itemView.findViewById(R.id.txtGname);
    }
}
