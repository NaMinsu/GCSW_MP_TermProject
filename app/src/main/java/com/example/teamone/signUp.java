package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class signUp extends AppCompatActivity {

    EditText idText;
    EditText passwordText;
    EditText passwordCheckText;
    Button signUpBtn;

    String ID, PW, PWCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        idText = (EditText) findViewById(R.id.signUpId);
        passwordText = (EditText) findViewById(R.id.signUpPW);
        passwordCheckText = (EditText) findViewById(R.id.signUpPWCheck);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 아이디, 비밀번호, 비밀번호 확인 문자열
                ID = idText.getText().toString();
                PW = passwordText.getText().toString();
                PWCheck = passwordCheckText.getText().toString();

                /*
                1. 비밀번호와 비밀번호 확인이 같다면 회원가입 시도 후, 로그인 화면으로 넘어감
                2. 비밀번호와 비밀번호 확인이 다르다면 Toast 알림
                 */
                if (PW.equals(PWCheck)) {
                    if(ID.length()!=0&&PW.length()!=0) {

                        //회원가입 시도

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(signUp.this, "Check your Password and Password Check", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}