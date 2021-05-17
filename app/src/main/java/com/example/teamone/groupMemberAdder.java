package com.example.teamone;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    ArrayList<String> Friend_DBEmails = new ArrayList<>();
    ArrayList<String> friends = new ArrayList<>();
    groupMemberAdderAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference groupRef = database.getReference("grouplist");
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference UsersGroupRef = database.getReference("UsersGroupInfo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupmemberadder);
        View selfLayout = findViewById(R.id.gmAdder);

        Intent intent = getIntent();
        String RoomName = intent.getStringExtra("name"); //방이름
        String timeCode = intent.getStringExtra("code");

        friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot friend : task.getResult().getChildren()) {
                    Friend_DBEmails.add(friend.getKey());
                }
            }
        });
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                for (String getEmail : Friend_DBEmails) {
                    if (task.getResult().hasChild(getEmail)) { // 친구 이름을 친구가 설정한 닉네임으로 불러오게끔 수정하였습니다
                        friends.add(task.getResult().child(getEmail).child("nickname").getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        RecyclerView rcview = findViewById(R.id.friendList);
        rcview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new groupMemberAdderAdapter(friends);
        rcview.setAdapter(adapter);

        okB = (Button) selfLayout.findViewById(R.id.btnOK);
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
                        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                                String fname = box.getText().toString();
                                //Toast.makeText(getApplicationContext(),fname,Toast.LENGTH_SHORT).show();
                                for (String getEmail : Friend_DBEmails) {
                                    if (task.getResult().hasChild(getEmail)) { //<-혹시나 모르는 중간에 탈퇴하는 회원을 위해 한번 더 검사
                                        if (box.isChecked() && task.getResult().child(getEmail).child("nickname").getValue().toString().equals(fname)) {
                                            UsersGroupRef.child(getEmail).child(timeCode).child("name").setValue(RoomName); /*친구의 그룹리스트에 해당그룹 추가 (친구화면에서 보이게)*/
                                            groupRef.child(timeCode).child("members").child("email").setValue(getEmail); /*맴버 리스트에 추가 (맴버들의 데이터 접근이 쉽게 DBEmail 로 */
                                        }
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
