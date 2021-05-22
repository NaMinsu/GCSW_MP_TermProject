package com.example.teamone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/*

그룹 list가 안읽어와져서 뒷부분 진행하기 위해서 현재 DB에 맞게 onChildAdded에서만 읽어올 수 있도록하고 진행했습니다.

*/


public class FragmentGroupList extends Fragment {
    ArrayList<String> groupItems = new ArrayList<>();
    groupAdapter adapter;
    static HashMap<String, ArrayList<String>> groupMap = new HashMap<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersGroupRef = database.getReference("UsersGroupInfo"); // 기존의 그룹리스트 참조하는 곳이 있다면 없애야 합니다

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grouplist, container, false);

        View selfLayout = v.findViewById(R.id.glLayout);

        RecyclerView rcView = v.findViewById(R.id.rcViewGroup);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        UsersGroupRef.child(FirstAuthActivity.getMyID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.hasChild("name")) {

                    String gname = snapshot.child("name").getValue().toString();
                    String code = snapshot.getKey();
                    String groupData = code + "@Admin_split@" + gname; // 방이름으로 사용하기 힘든 문자로 붙였습니다
                    if (!groupItems.contains(groupData)) { /* 중복 입력 방지 */
                        groupItems.add(groupData);
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
                intent.putExtra("GroupData",groupItems);
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

        if (requestCode == 1) { //groupAdder
            if (resultCode == RESULT_OK) {

                String gName = data.getStringExtra("groupName");
                String gCode = data.getStringExtra("groupCode");
                ArrayList<String> gfriends = data.getStringArrayListExtra("selfriends");
                String groupData = gCode + "@Admin_split@" + gName;

                groupMap.put(gName, gfriends);
                if (!groupItems.contains(groupData)) { /* 중복 입력 방지 */
                    groupItems.add(groupData);
                }

            }
        } else if (requestCode == 2) { // groupDeleter
            if (resultCode == RESULT_OK) {
                String Find_group = data.getStringExtra("groupInfo");
                if (groupItems.contains(Find_group)) {
                    groupItems.remove(Find_group);
                } else
                    Toast.makeText(getActivity(), "해당 그룹이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }


    public static HashMap<String, ArrayList<String>> getGroupMap() {
        return groupMap;
    }

}