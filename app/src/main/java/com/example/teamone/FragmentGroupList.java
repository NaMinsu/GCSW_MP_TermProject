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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/*

그룹 list가 안읽어와져서 뒷부분 진행하기 위해서 현재 DB에 맞게 onChildAdded에서만 읽어올 수 있도록하고 진행했습니다.

*/


public class FragmentGroupList extends Fragment {
    ArrayList<String> groupItems = new ArrayList<String>();
    groupAdapter adapter;
    static HashMap<String, ArrayList<String>> groupMap = new HashMap<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference groupRef = database.getReference("grouplist");

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grouplist,container,false);

        View selfLayout = v.findViewById(R.id.glLayout);

        RecyclerView rcView = v.findViewById(R.id.rcViewGroup);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupRef.child(FirstAuthActivity.getMyID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if(snapshot.hasChild("name")) {
                    String gname = snapshot.child("name").getValue().toString();
                    if(!groupItems.contains(gname)){ // 중복 입력 방지
                         groupItems.add(gname);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        adapter = new groupAdapter(getActivity(), groupItems);
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