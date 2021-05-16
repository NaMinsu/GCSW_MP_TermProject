package com.example.teamone;


import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class groupMemberAdder extends AppCompatActivity {

    View selfLayout;
    Button okB, cancelB;
    ArrayList<String> friends = new ArrayList<>();
    groupMemberAdderAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference groupRef = database.getReference("grouplist");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupmemberadder);
        View selfLayout = findViewById(R.id.gmAdder);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot friend : task.getResult().getChildren()) {
                    friends.add(friend.child("name").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }
        });

        RecyclerView rcview = findViewById(R.id.friendList);
        rcview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new groupMemberAdderAdapter(friends);
        rcview.setAdapter(adapter);

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CheckBox> list = adapter.getChecklist();
                Boolean isCheckOne = false;

                for (CheckBox box : list) {
                    if (box.isChecked()) {
                        isCheckOne = true;
                        break;
                    }
                }
                if (!isCheckOne)
                    Toast.makeText(getApplicationContext(), "선택된 친구가 없습니다.", Toast.LENGTH_SHORT).show();
                else {
                    for (CheckBox box : list) {
                        friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                String fname = box.getText().toString();
                                Toast.makeText(getApplicationContext(),fname,Toast.LENGTH_SHORT).show();
                                for (DataSnapshot friend : task.getResult().getChildren()) {
                                    if (box.isChecked() && friend.child("name").getValue().toString().equals(fname)) {
                                        groupRef.child(FirstAuthActivity.getMyID()).child(name).child(fname).
                                                child("email").setValue(friend.child("email").getValue().toString());
                                        groupRef.child(FirstAuthActivity.getMyID()).child(name).child(fname).
                                                child("name").setValue(friend.child("name").getValue().toString());
                                    }
                                }
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(), "선택한 친구가 그룹에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


        cancelB = (Button)selfLayout.findViewById(R.id.btnCancel);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
