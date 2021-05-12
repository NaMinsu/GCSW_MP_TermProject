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

public class FriendListFragment extends Fragment {
    ArrayList<String> friendList = new ArrayList<>();
    friendAdapter adapter;
    private static HashMap<String, String> infoTable = new HashMap<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_friendlist,container,false);
        View selfLayout = v.findViewById(R.id.flLayout);

        RecyclerView rcView = v.findViewById(R.id.rcViewFriend);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendshipRef.child(FirstAuthActivity.getMyID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.hasChild("name") && snapshot.hasChild("email")) {
                    String fname = snapshot.child("name").getValue().toString();
                    String fid = snapshot.child("email").getValue().toString();

                    friendList.add(fname);
                    infoTable.put(fname, fid);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.hasChild("name") && snapshot.hasChild("email")) {
                    String fname = snapshot.child("name").getValue().toString();
                    String fid = snapshot.child("email").getValue().toString();

                    friendList.add(fname);
                    infoTable.put(fname, fid);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        adapter = new friendAdapter(getActivity(), friendList);
        rcView.setAdapter(adapter);

        Button friendAddB = (Button)selfLayout.findViewById(R.id.btnAddFriend);
        friendAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), friendAdder.class);
                startActivityForResult(intent, 1);
            }
        });


       return v;
    }

    @Override
    public void onStart() {
        super.onStart();
       adapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
        friendList.clear();
        // When Fragment was not seen on the screen, cleared the list.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String fName = data.getStringExtra("friendName");
            Toast.makeText(getActivity(), fName + "님을 추가했습니다.", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    public static HashMap<String, String> getInfoTable() {
        return infoTable;
    }
}