package com.example.teamone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText idText;
    EditText pwText;
    Button logInButton;
    TextView signUpText;

    String ID, PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = (EditText)findViewById(R.id.logInID);
        pwText = (EditText)findViewById(R.id.logInPW);
        logInButton = (Button)findViewById(R.id.logInBtn);
        signUpText = (TextView)findViewById(R.id.singUpTxt);


        /*
        로그인 버튼이 눌렸을 경우
         */
        logInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //아이디, 비밀번호 문자열
                ID = String.valueOf(idText.getText());
                PW = String.valueOf(pwText.getText());


                /*
                로그인 성공시 메인 액티비티로 넘어감
                실패시 Toast 알림
                 */
                if(true){
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                }else if(false){
                    Toast.makeText(LoginActivity.this, "Check your ID or Password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*
        회원가입 텍스트가 눌렸을 경우
         */
        signUpText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),signUp.class);
                startActivity(intent);
            }
        });




    }
}
