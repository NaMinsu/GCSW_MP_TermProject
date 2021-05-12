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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class friendAdder extends Activity {
    View selfLayout;
    Button okB, cancelB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference userRef = database.getReference("users");
    protected boolean isInDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendadder);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        EditText fnameTxt = (EditText) findViewById(R.id.txtFname);
        EditText fmailTxt = (EditText) findViewById(R.id.txtAccount);
        String MY_EMAIL = sf.getString("Email", "");
        selfLayout = findViewById(R.id.fAdder);

        okB = (Button) selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fnameTxt.getText().toString();
                String fmail = fmailTxt.getText().toString().replace(".", "_");

                if (fmail.equals(MY_EMAIL)) {
                    Toast.makeText(friendAdder.this, "당신의 계정입니다", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }

                userRef.child(fmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (null == snapshot.getValue()) {
                            Toast.makeText(friendAdder.this, "존재하지 않는 계정입니다", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_CANCELED);
                        } else {
                            friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DataSnapshot ds = task.getResult();
                                        if (ds.hasChild(fmail))
                                            isInDB = true;
                                    }
                                }
                            });

                            if (!isInDB) {
                                friendshipRef.child(FirstAuthActivity.getMyID()).child(fmail).child("email").setValue(fmail.replace("_", "."));
                                friendshipRef.child(FirstAuthActivity.getMyID()).child(fmail).child("name").setValue(fName);
                                Intent intent = new Intent(getApplicationContext(), FriendListFragment.class);
                                intent.putExtra("friendName", fName);
                                setResult(RESULT_OK, intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "이미 등록된 친구입니다.", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                            }

                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
}