package com.example.teamone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MakeGroupAdapter extends RecyclerView.Adapter<MGViewHolder>{
    ArrayList<String> dataList;
    private static ArrayList<CheckBox> checklist = new ArrayList<>();

    public MakeGroupAdapter(ArrayList<String> list) {
        dataList = list;
    }

    @NonNull
    @Override
    public MGViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_groupfriends, parent, false);
        MGViewHolder mgv = new MGViewHolder(view);

        return mgv;
    }

    @Override
    public void onBindViewHolder(@NonNull MGViewHolder holder, int position) {
        holder.onBind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList == null)
            return 0;
        else
            return dataList.size();
    }

    public ArrayList<CheckBox> getChecklist() {
        return checklist;
    }

    public static void setChecklist(CheckBox state) {
        checklist.add(state);
    }
}

class MGViewHolder extends RecyclerView.ViewHolder {
    CheckBox fbox;

    public MGViewHolder(@NonNull View itemView) {
        super(itemView);
        fbox = itemView.findViewById(R.id.target);

        MakeGroupAdapter.setChecklist(fbox);
    }

    public void onBind(String datatxt) { fbox.setText(datatxt); }
}