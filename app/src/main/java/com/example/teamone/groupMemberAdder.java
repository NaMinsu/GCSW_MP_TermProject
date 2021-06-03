package com.example.teamone;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// for add groupmember in group table
public class groupMemberAdder extends Activity {

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
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFunctions = FirebaseFunctions.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupmemberadder);
        View selfLayout = findViewById(R.id.gmAdder);

        Intent intent = getIntent();
        String GroupName = intent.getStringExtra("name"); //group name
        String timeCode = intent.getStringExtra("code"); // group's unique code

        TextView groupNames = (TextView)findViewById(R.id.GroupNames);
        groupNames.setText(GroupName);

        //read all the friends list of the user
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
                    if (task.getResult().hasChild(getEmail)) { // Show friend's name by friend's nickname they made
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
                                    if (task.getResult().hasChild(getEmail)) { //<-One more inspection for members who leave in the middle of an unexpected situation
                                        if (box.isChecked() && task.getResult().child(getEmail).child("nickname").getValue().toString().equals(fname)) {
                                            UsersGroupRef.child(getEmail).child(timeCode).child("name").setValue(GroupName); /*Add a group to a friend's group list (visible on the Friends screen)*/
                                            groupRef.child(timeCode).child("members").child(getEmail).child("email").setValue(getEmail); /*Add to member list (to DBEmail for easy access to members' data)*/
                                            if (task.getResult().child(getEmail).hasChild("token")) {
                                                String token = task.getResult().child(getEmail).child("token").getValue().toString();
                                                On_MakeNotification(token, GroupName, "새로운 그룹에 초대되었습니다.", "TeamOne");
                                            }
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
        }); // add new member who checked by the current user and store the data in DB


        cancelB = (Button)selfLayout.findViewById(R.id.btnCancel);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Task<String> sendFCM(String regToken, String title, String message, String SubTitle) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", regToken);
        data.put("text", message);
        data.put("title", title);
        data.put("subtext", SubTitle);
        data.put("android_channel_id", "Group");

        return mFunctions
                .getHttpsCallable("sendFCM")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) Objects.requireNonNull(task.getResult()).getData().toString();
                        Log.d("SendPush", "then: " + result);
                        return result;
                    }
                });
    }

    private void On_MakeNotification(String token, String Title, String text, String SubTitle) {
        sendFCM(token, Title, text, SubTitle)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();

                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            Log.w("SendPush", "makeNotification:onFailure", e);
                            return;
                        }

                        String result = task.getResult();

                    }
                });

    }
}
