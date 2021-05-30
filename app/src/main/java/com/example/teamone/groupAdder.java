package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class groupAdder extends Activity {
    String MyToken;
    View selfLayout;
    Button okB, cancelB;
    ArrayList<String> Friend_DBEmails = new ArrayList<>();
    ArrayList<String> friends = new ArrayList<>();
    MakeGroupAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference UserGroupInfoRef = database.getReference("UsersGroupInfo");
    DatabaseReference groupRef = database.getReference("grouplist");
    SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.KOREA);
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupadder);
        mFunctions = FirebaseFunctions.getInstance();
        selfLayout = findViewById(R.id.gAdder);
        EditText gnameTxt = (EditText) selfLayout.findViewById(R.id.txtGname);
        MyToken  = "no Token";
        String MyID=FirstAuthActivity.getMyID();

        friendshipRef.child(MyID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot friend : task.getResult().getChildren()) {
                    Friend_DBEmails.add(friend.getKey()); // Friends' db emails.
                }

            }
        });
        /*Nicknames are data that members can change frequently,
         so they are not saved separately,
         but they are recalled from user information whenever they are loaded onto the screen.*/
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
                if (gName.equals(""))
                    Toast.makeText(getApplicationContext(), "그룹명은 반드시 입력해야합니다.", Toast.LENGTH_SHORT).show();
                else if (!isCheckOne)
                    Toast.makeText(getApplicationContext(), "친구는 반드시 한명 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                else {
                    Calendar c = Calendar.getInstance();
                    String datetime = DateFormat.format(c.getTime());
                    UserGroupInfoRef.child(MyID).child(datetime).child("name").setValue(gName);

                    for (CheckBox box : list) {
                        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                String fName = box.getText().toString();
                                for (String getEmail : Friend_DBEmails) {
                                    if (task.getResult().hasChild(getEmail)) { //<-One more inspection for members who leave in the middle of an unexpected situation.
                                        if (box.isChecked() && task.getResult().child(getEmail).child("nickname").getValue().toString().equals(fName)) {
                                            UserGroupInfoRef.child(getEmail).child(datetime).child("name").setValue(gName);
                                            groupRef.child(datetime).child("members").child(getEmail).child("email").setValue(getEmail);
                                            groupRef.child(datetime).child("members").child(getEmail).child("WantPush").setValue("1");
                                            if (task.getResult().child(getEmail).hasChild("token")) {
                                                String token = task.getResult().child(getEmail).child("token").getValue().toString();
                                                On_MakeNotification(token, gName, "새로운 그룹에 초대되었습니다.", "TeamOne");
                                            }
                                        }
                                    }
                                }
                                if (task.getResult().child(MyID).hasChild("token")&&MyToken.equals("no Token")){
                                    MyToken = task.getResult().child(MyID).child("token").getValue().toString();
                                    groupRef.child(datetime).child("members").child(MyID).child("WantPush").setValue("1");
                                    On_MakeNotification(MyToken, gName, "새로운 그룹이 생성되었습니다.", "TeamOne");
                                }
                            }
                        });
                    }
                    groupRef.child(datetime).child("members").child(MyID).child("email").setValue(MyID); /*Add a group to the creator member*/
                    groupRef.child(datetime).child("GroupName").child("name").setValue(gName);
                    groupRef.child(datetime).child("GroupSchedule").child("schedule").setValue("0");
                    MyToken = "no Token";
                    Intent intent = new Intent(getApplicationContext(), FragmentGroupList.class);
                    intent.putExtra("groupName", gName);
                    intent.putExtra("groupCode", datetime);
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
    public void onBackPressed() {
        return;
    }

    public Task<String> sendFCM(String regToken, String title, String message, String SubTitle) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", regToken);
        data.put("text", message);
        data.put("title", title); // Group Name
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