package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class groupAdder extends Activity {
    View selfLayout;
    Button okB, cancelB;
    ArrayList<String> Friend_DBEmails = new ArrayList<>();
    ArrayList<String> friends = new ArrayList<>();
    MakeGroupAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference groupRef = database.getReference("UsersGroupInfo");
    SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss", Locale.KOREA);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupadder);
        selfLayout = findViewById(R.id.gAdder);
        EditText gnameTxt = (EditText)selfLayout.findViewById(R.id.txtGname);



        friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot friend : task.getResult().getChildren()) {
                    Friend_DBEmails.add(friend.getKey()); // 친구들의 db 이메일들
                }

            }
        });
        // 닉네임은 회원이 자주 바꿀 수 있는데이터라,따로 저장해놓지 않고 화면에 정보를 불러올때마다 유저정보에서 불러와주었습니다
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(String getEmail: Friend_DBEmails) {
                    if(task.getResult().hasChild(getEmail)){
                        friends.add(task.getResult().child(getEmail).child("nickname").getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        RecyclerView rcview = findViewById(R.id.friendList);
        rcview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MakeGroupAdapter(friends);
        rcview.setAdapter(adapter);

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gName = gnameTxt.getText().toString();
                ArrayList<CheckBox> list = adapter.getChecklist();
                Boolean isCheckOne = false;

                for (CheckBox box : list) {
                    if (box.isChecked()) {
                        isCheckOne = true;
                        break;
                    }
                }
                      // 그룹 장 어찌할지 정하기 (추가자) 친구를 고르고 있을때 이메일이 바뀌면 어떡하지 ?
                if (gName.equals(""))
                    Toast.makeText(getApplicationContext(), "그룹명은 반드시 입력해야합니다.", Toast.LENGTH_SHORT).show();
                else if (!isCheckOne)
                    Toast.makeText(getApplicationContext(), "친구는 반드시 한명 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                else {
                    Calendar c = Calendar.getInstance();
                    String datetime = DateFormat.format(c.getTime());
                    groupRef.child(FirstAuthActivity.getMyID()).child(datetime).child("name").setValue(gName);

                    for (CheckBox box : list) {
                        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                String fName = box.getText().toString();
                                for(String getEmail: Friend_DBEmails) {
                                    if(task.getResult().hasChild(getEmail)){ //<-혹시나 모르는 중간에 탈퇴하는 회원을 위해 한번 더 검사
                                        if(box.isChecked()&& task.getResult().child(getEmail).child("nickname").getValue().toString().equals(fName)){
                                             groupRef.child(getEmail).child(datetime).child("name").setValue(gName);
                                        }
                                    }
                                }
//                                for (DataSnapshot friend : task.getResult().getChildren()) { // 나중에 이 코드 참고해서 그룹리스트 만들기
//                                    if (box.isChecked() && friend.child("nickname").getValue().toString().equals(fname)) {
//
//
//                                        groupRef.child(FirstAuthActivity.getMyID()).child(gName).child(fname).
//                                                child("email").setValue(friend.child("email").getValue().toString());
//                                        groupRef.child(FirstAuthActivity.getMyID()).child(gName).child(fname).
//                                                child("name").setValue(friend.child("name").getValue().toString());
//                                    }
//                                }
                            }
                        });
                    }

                    Intent intent = new Intent(getApplicationContext(), FragmentGroupList.class);
                    intent.putExtra("groupName", gName);
                      ArrayList<String> selected = new ArrayList<>();
                    for (CheckBox box : list) {
                        if (box.isChecked())
                            selected.add(box.getText().toString());
                    }
                    intent.putExtra("selfriends", selected);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        cancelB = (Button)selfLayout.findViewById(R.id.btnCancel);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    public void onBackPressed() { return; }
}