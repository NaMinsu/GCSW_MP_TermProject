package com.gcsw.teamone;

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

    // constructor of adapter
    friendAdapter(Context c, ArrayList<String> list) {
        mContext = c; // context of adapted activity
        dataList = list; // list of friends to show
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        // inflation
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // setup recyclerview item
        View view = inflater.inflate(R.layout.items_friend, parent, false);
        FriendViewHolder cvh = new FriendViewHolder(view, mContext);

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        // show data in view
        holder.onBind((String)dataList.get(position));
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

class FriendViewHolder extends RecyclerView.ViewHolder {
    public TextView txt_name; // friend name textview
    Context ctx;

    // constructor of viewholder
    public FriendViewHolder(@NonNull View itemView, Context c) {
        super(itemView);
        ctx = c;
        txt_name = itemView.findViewById(R.id.friendName);

        // put OnClickListener to process click event
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a friend is clicked, show pop-up of friend information
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(ctx, friendInfo.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("friendname", txt_name.getText().toString());
                    ctx.startActivity(intent);
                }
            }
        });
    }

    // the function to show friend name
    public void onBind(String dataTxt) {
        txt_name.setText(dataTxt);
    }

}
