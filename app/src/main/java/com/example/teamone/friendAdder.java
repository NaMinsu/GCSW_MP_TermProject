package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Hashtable;
import java.util.Objects;

public class friendAdder extends Activity {
    View selfLayout;
    int friendID_check;
    Button okB, cancelB;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendadder);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        EditText fnameTxt = (EditText)findViewById(R.id.txtFname);
        EditText accountTxt = (EditText)findViewById(R.id.txtAccount);
        selfLayout = findViewById(R.id.fAdder);
        database = FirebaseDatabase.getInstance();
        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fnameTxt.getText().toString();
                String fEmail = accountTxt.getText().toString();

                String MY_EMAIL = sf.getString("Email", "");
                if(fEmail.equals(MY_EMAIL)){
                    Toast.makeText(friendAdder.this, "당신의 계정입니다", Toast.LENGTH_SHORT).show(); // 무슨말을 적어야 할지 고민됩니다
                    return;
                }
                String[] emailID = MY_EMAIL.split("\\.");
                String DBEmail = emailID[0] + "_" + emailID[1];


                if(FriendIDCheck(fEmail)){
                    // 이미 추가한 친구를 확인하고 "이미 친구입니다" 추가 고려중입니다
                    String[] F_emailID = fEmail.split("\\.");
                    String F_DBEmail = F_emailID[0] + "_" + F_emailID[1];
                    DatabaseReference friendRef = database.getReference("friendship").child(DBEmail).child(F_DBEmail); // DB setValue 할 주소입니다
                    Hashtable<String, String> Message_log
                            = new Hashtable<>();

                    Message_log.put("email", fEmail);
                    Message_log.put("name", fName);
                    friendRef.setValue(Message_log);

                    Intent intent = new Intent(getApplicationContext(), friendList.class);
                    intent.putExtra("friendName", fName);
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
    public boolean FriendIDCheck(String email_to_check){

        DatabaseReference Search_friend = database.getReference("users");

        Search_friend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) { //dataSnapshot1 = Search_friend 의 child
                    try {
                        if (Objects.equals(dataSnapshot1.child("email").getValue(),email_to_check)) { // Search_friend 의 child 안에 있는 email 과 비교
                            friendID_check = 1;
                        }
                    } catch (NullPointerException ignored) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        if(friendID_check != 1){
            Toast.makeText(friendAdder.this, "존재하지 않는 회원입니다", Toast.LENGTH_SHORT).show();
            /*아직은 한번 눌렀을 때 존재하지 않는 회원입니다 가 나왔다가 다시 눌렀을 때 적용됩니다
             실행순서를 addListenerForSingleValueEvent 가 끝날 때 까지 기다렸다가 하게 만들어보겠습니다 */
            return false;
        }
        else{
            return true;
        }
    }
}