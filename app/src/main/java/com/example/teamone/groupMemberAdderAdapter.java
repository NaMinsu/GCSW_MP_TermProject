package com.example.teamone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//Adapter for groupmember activity. Show friend list by this adapter
public class groupMemberAdderAdapter extends RecyclerView.Adapter<GMAViewHolder>{

    ArrayList<String> dataList;
    private static ArrayList<CheckBox> checklist = new ArrayList<>();

    public groupMemberAdderAdapter(ArrayList<String> list) {dataList = list;}
    //dataList is a data for store user's friends email

    @NonNull
    @Override
    public GMAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewGroup that show friends' information to user
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_groupfriends, parent, false);
        GMAViewHolder mgv = new  GMAViewHolder(view);

        return mgv;
    }

    @Override
    public void onBindViewHolder(@NonNull GMAViewHolder holder, int position) {
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

    class GMAViewHolder extends RecyclerView.ViewHolder{
        CheckBox fbox;
        public GMAViewHolder(@NonNull View itemView) {
            super(itemView);
            fbox = itemView.findViewById(R.id.target);

            groupMemberAdderAdapter.setChecklist(fbox);
        }


    public void onBind(String datatxt) { fbox.setText(datatxt); }
}
