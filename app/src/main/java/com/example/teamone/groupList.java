package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class groupList extends AppCompatActivity {
    ArrayList<String> groupItems;
    groupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);
        View selfLayout = (View)findViewById(R.id.glLayout);

        groupItems = new ArrayList<String>();
        adapter = new groupAdapter(groupItems);
        RecyclerView rcView = findViewById(R.id.rcViewGroup);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        rcView.setAdapter(adapter);

        Button groupAddB = (Button)selfLayout.findViewById(R.id.btnAddGroup);
        groupAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupAdder.class);
                startActivityForResult(intent, 1);
            }
        });

        Button groupDeleteB = (Button)selfLayout.findViewById(R.id.btnDeleteGroup);
        groupDeleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupDeleter.class);
                startActivityForResult(intent, 2);
            }
        });

        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String gName = data.getStringExtra("groupName");
                groupItems.add(gName);
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String gName = data.getStringExtra("groupName");
                if (groupItems.contains(gName))
                    groupItems.remove(gName);
                else
                    Toast.makeText(getApplicationContext(), "해당 그룹이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }
}