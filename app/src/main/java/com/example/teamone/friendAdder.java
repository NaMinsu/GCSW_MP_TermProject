package com.example.teamone;

import android.content.Intent;
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

public class friendAdder extends AppCompatActivity {
    View selfLayout = findViewById(R.id.fAdder);
    Button okB, cancelB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendadder);

        EditText fnameTxt = (EditText)findViewById(R.id.txtFname);

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fnameTxt.getText().toString();
                Intent intent = new Intent(getApplicationContext(), friendList.class);
                intent.putExtra("groupName", fName);
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