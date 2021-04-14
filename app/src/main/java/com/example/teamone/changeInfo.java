package com.example.teamone;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class changeInfo extends AppCompatActivity {

    Button btnNickname,btnSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        View selfLayout = (View) findViewById(R.id.ciLayout);

        btnNickname = (Button)selfLayout.findViewById(R.id.btnNicknameChange);


        btnSchool = (Button)selfLayout.findViewById(R.id.btnSchoolChange);

    }
}
