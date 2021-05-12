package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class GroupListFragment extends Fragment {
    ArrayList<String> groupItems = new ArrayList<String>();
    groupAdapter adapter;
    static HashMap<String, ArrayList<String>> groupMap = new HashMap<>();

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grouplist,container,false); // 이름 fragment_grouplist  로 수정예정

        View selfLayout = v.findViewById(R.id.glLayout);

        adapter = new groupAdapter(getActivity(), groupItems);
        RecyclerView rcView = v.findViewById(R.id.rcViewGroup);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(adapter);

        Button groupAddB = (Button)selfLayout.findViewById(R.id.btnAddGroup);
        groupAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), groupAdder.class);
                startActivityForResult(intent, 1);
            }
        });

        Button groupDeleteB = (Button)selfLayout.findViewById(R.id.btnDeleteGroup);
        groupDeleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), groupDeleter.class);
                startActivityForResult(intent, 2);
            }
        });


        return v;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_grouplist);
//        View selfLayout = (View)findViewById(R.id.glLayout);
//
//        adapter = new groupAdapter(getApplicationContext(), groupItems);
//        RecyclerView rcView = findViewById(R.id.rcViewGroup);
//        rcView.setLayoutManager(new LinearLayoutManager(this));
//        rcView.setAdapter(adapter);
//
//        Button groupAddB = (Button)selfLayout.findViewById(R.id.btnAddGroup);
//        groupAddB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), groupAdder.class);
//                startActivityForResult(intent, 1);
//            }
//        });
//
//        Button groupDeleteB = (Button)selfLayout.findViewById(R.id.btnDeleteGroup);
//        groupDeleteB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), groupDeleter.class);
//                startActivityForResult(intent, 2);
//            }
//        });
//
//        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);
//        myPageB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        Button groupB = (Button)selfLayout.findViewById(R.id.btnGroup);
//
//        Button friendB = (Button)selfLayout.findViewById(R.id.btnFriend);
//        friendB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), friendList.class);
//                startActivity(intent);
//            }
//        });
//
//        Button settingB = (Button)selfLayout.findViewById(R.id.btnSetUp);
//        settingB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), settings.class);
//                startActivity(intent);
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String gName = data.getStringExtra("groupName");
                ArrayList<String> gfriends = data.getStringArrayListExtra("selfriends");
                groupMap.put(gName, gfriends);
                groupItems.add(gName);
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String gName = data.getStringExtra("groupName");
                if (groupItems.contains(gName))
                    groupItems.remove(gName);
                else
                    Toast.makeText(getActivity(), "해당 그룹이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }

    public static HashMap<String, ArrayList<String>> getGroupMap() {
        return groupMap;
    }
}