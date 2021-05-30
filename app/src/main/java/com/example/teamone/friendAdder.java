package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
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
    MediaPlayer mediaPlayer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendshipRef = database.getReference("friendship");
    DatabaseReference userRef = database.getReference("users");
    boolean isInDB = false;
    String fName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendadder);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        EditText fmailTxt = (EditText) findViewById(R.id.txtAccount);
        String MY_EMAIL = sf.getString("Email", "");
        selfLayout = findViewById(R.id.fAdder);
        mediaPlayer = MediaPlayer.create(this, R.raw.quietswitch);
        okB = (Button) selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                String fmail = fmailTxt.getText().toString().replace(".", "_");
                /*
                메일이 그냥 띄어쓰기만 있다거나 비어있으면 null point 에러가 발생해서 해당 코드를 삽입했습니다.
                문제가 발생하면 if~else{삭제하시고 98줄에 있는 } 삭제해주시면 됩니다
                 */
                if (fmail.trim().equals("")) {
                    Toast.makeText(friendAdder.this, "계정을 입력해주세요", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                } else {
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
                                if (snapshot.getKey().equals(FirstAuthActivity.getMyID())) {
                                    Toast.makeText(friendAdder.this, "본인 계정은 친구로 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_CANCELED);
                                } else {
                                    fName = snapshot.child("nickname").getValue().toString();
                                    friendshipRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DataSnapshot ds = task.getResult();
                                                if (ds.hasChild(fmail))
                                                    isInDB = !isInDB;
                                                if (!isInDB) {
                                                    friendshipRef.child(FirstAuthActivity.getMyID()).child(fmail).child("email").setValue(fmail.replace("_", "."));
                                                    friendshipRef.child(FirstAuthActivity.getMyID()).child(fmail).child("name").setValue(fName);
                                                    Intent intent = new Intent(getApplicationContext(), FragmentFriendList.class);
                                                    intent.putExtra("friendName", fName);
                                                    setResult(RESULT_OK, intent);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "이미 등록된 친구입니다.", Toast.LENGTH_SHORT).show();
                                                    setResult(RESULT_CANCELED);
                                                }
                                            }
                                        }
                                    });
                                }
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        cancelB = (Button) selfLayout.findViewById(R.id.btnCancel);
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
    private void killMediaPlayer(){
        if(mediaPlayer!=null){
            try{mediaPlayer.release();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }
}