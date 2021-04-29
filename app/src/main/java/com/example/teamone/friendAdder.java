package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class friendAdder extends Activity {
    View selfLayout;
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

                String MY_EMAIL=sf.getString("Email","");
                String[] emailID = MY_EMAIL.split("\\.");
                String DBEmail = emailID[0]+"_"+emailID[1];

                String[] F_emailID = fEmail.split("\\.");
                String F_DBEmail = emailID[0]+"_"+emailID[1];
                DatabaseReference friendRef = database.getReference("friendship").child(DBEmail).child(F_DBEmail);
                Hashtable<String, String> Message_log
                        = new Hashtable<>();

                Message_log.put("email",fEmail);
                Message_log.put("name",fName);
                friendRef.setValue(Message_log);

                Intent intent = new Intent(getApplicationContext(), friendList.class);
                intent.putExtra("friendName", fName);
                setResult(RESULT_OK, intent);
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