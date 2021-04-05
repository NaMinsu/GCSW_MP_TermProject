package com.example.teamone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText idText;
    EditText pwText;
    Button logInButton;
    TextView signUpText;

    String ID, PW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        idText = (EditText) findViewById(R.id.logInID);
        pwText = (EditText) findViewById(R.id.logInPW);
        logInButton = (Button) findViewById(R.id.logInBtn);
        signUpText = (TextView) findViewById(R.id.singUpTxt);


        /*
        로그인 버튼이 눌렸을 경우
         */
        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //아이디, 비밀번호 문자열
                ID = String.valueOf(idText.getText());
                PW = String.valueOf(pwText.getText());

                if (ID.length() != 0 && PW.length() != 0) {

                    mAuth.signInWithEmailAndPassword(ID, PW)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) { //로그인성공시
                                        //Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user.isEmailVerified()) {  // 이메일 인증을 완료하였다면

                                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(in);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "이메일 인증을 완료해주세요.", Toast.LENGTH_LONG).show();
                                        }
                                    } else { // 로그인 실패시
                                        //  Log.w(TAG, "signInWithEmail:failure", task.getException()); 실패 로그 부분
                                        Toast.makeText(LoginActivity.this, "Check your ID or Password", Toast.LENGTH_SHORT).show();
                                        //     updateUI(null);
                                    }
                                }
                            });

                }
            }
        });


        /*
        회원가입 텍스트가 눌렸을 경우
         */
        signUpText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //   updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        //로그인 화면 백버튼 시 이전 Activity 로 돌아가는 상황을 막기 위한 코드
        moveTaskToBack(true);
    }
}
