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

public class changeInfo extends AppCompatActivity {

    Button btnNickname,btnSchool;
    String Nickname,School;
    EditText nickname,school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        View selfLayout = (View) findViewById(R.id.ciLayout);

        nickname = (EditText)selfLayout.findViewById(R.id.nickValue);
        school = (EditText)selfLayout.findViewById(R.id.schoolValue);

        btnNickname = (Button)selfLayout.findViewById(R.id.btnNicknameChange);
        btnNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nickname = String.valueOf (nickname.getText());

                Intent intent = new Intent(getApplicationContext(), groupMemberAdder.class); 
                intent.putExtra("value",Nickname);
                startActivity(intent);
            }
        });


        School = String.valueOf(school.getText());
        btnSchool = (Button)selfLayout.findViewById(R.id.btnSchoolChange);
        btnSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                School = String.valueOf (school.getText());

                Intent intent = new Intent(getApplicationContext(), groupMemberAdder.class);
                intent.putExtra("value",School);
                startActivity(intent);
            }
        });

    }
}
