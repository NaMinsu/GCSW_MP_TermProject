package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

public class groupList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);
        View selfLayout = (View)findViewById(R.id.glLayout);

        ScrollView groups = (ScrollView)selfLayout.findViewById(R.id.glist);

        Button groupAddB = (Button)selfLayout.findViewById(R.id.btnAddGroup);
        groupAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupAdder.class);
                startActivityForResult(intent, 1);
            }
        });

        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), myPage.class);
                startActivity(intent);
            }
        });

        Button groupB = (Button)selfLayout.findViewById(R.id.btnGroup);

        Button friendB = (Button)selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), friendList.class);
                startActivity(intent);
            }
        });

        Button settingB = (Button)selfLayout.findViewById(R.id.btnSetUp);
        settingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), settings.class);
                startActivity(intent);
            }
        });

        RecyclerView listView = (RecyclerView)findViewById(R.id.rcViewGroup);
        listView.setHasFixedSize(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String gName = data.getStringExtra("groupName");
        }
        else if (resultCode == RESULT_CANCELED)
            return;
    }

    public class groupAdapter extends RecyclerView.Adapter<groupList.groupAdapter.CustomViewHolder> {

        @NonNull
        @Override
        public groupList.groupAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull groupList.groupAdapter.CustomViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}