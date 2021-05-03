package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

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

        EditText fnameTxt = (EditText)findViewById(R.id.txtFname);
        EditText fmailTxt = (EditText)findViewById(R.id.txtAccount);
        selfLayout = findViewById(R.id.fAdder);

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fnameTxt.getText().toString();
                String fmail = fmailTxt.getText().toString();

                friendshipRef.child(LoginActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            DataSnapshot ds = task.getResult();
                            if (ds.hasChild(fmail))
                                isInDB = true;
                        }
                    }
                });

                if (!isInDB) {
                    friendshipRef.child(LoginActivity.getMyID()).child(fmail).setValue(fmail);
                    Intent intent = new Intent(getApplicationContext(), friendList.class);
                    intent.putExtra("friendName", fName);
                    setResult(RESULT_OK, intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 등록된 친구입니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }

                finish();
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