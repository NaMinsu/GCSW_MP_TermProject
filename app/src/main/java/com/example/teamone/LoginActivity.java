package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Hashtable;

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
                                            String stUserEmail = user.getEmail();
                                            SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sf.edit();
                                            editor.putString("Email", stUserEmail);
                                            editor.commit();
                                            /*https://jhshjs.tistory.com/56  SharedPreferences 참고
                                              사용자가 앱을 지우기 전까지 기기속에 저장되는 데이터
                                              로그인 할 때 이메일을 저장시켜, 앱 들어올 때 시작화면에서
                                               정보 확인 후 이메일 정보가 있으면 로그인건너뛰는 식으로 만들었습니다.*/

                                            String[] emailID = stUserEmail.split("\\.");
                                            String DBEmail = emailID[0] + "_" + emailID[1];
                                            DatabaseReference users_ref = database.getReference("users").child(DBEmail);
                                            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    // Log.d(TAG, "onDataChange: " + snapshot.getValue().toString());
                                                    User_Item nicknameCamera = snapshot.getValue(User_Item.class);
                                                    /*프로필 사진을 불러오는 시간을 줄이려고 로그인 할 때 불러왔습니다
                                                     이럴 시 ,다른 기기에서 같은 계정으로 동시에 로그인하고,
                                                     프로필 사진을 한쪽기기에서만  바꾸는 상황이 온다면 다시 로그인 해야 다른기기에도 적용됩니다
                                                     실제 앱이라면 동시 로그인을 막거나, 조치를 취하겠지만 아직은 정해진게 없어
                                                      이렇게 두겠습니다.
                                                      */
                                                    editor.putString("profile_image", nicknameCamera.getProfile_image());
                                                    editor.commit();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            FirebaseMessaging.getInstance().getToken()
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            if (!task.isSuccessful()) {
                                                                Log.w("LoginActivity", "Fetching FCM registration token failed", task.getException());
                                                                return;
                                                            }
                                                            // Get new FCM registration token
                                                            String token = task.getResult();
                                                            DatabaseReference usersToken_ref =
                                                                    FirebaseDatabase.getInstance().getReference("users").child(DBEmail).child("token");
                                                            usersToken_ref.setValue(token);
                                                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();

                                                        }
                                                    });
                                            Intent in = new Intent(LoginActivity.this, FirstAuthActivity.class); // 첫 로그인시에 데이터 꼬이는 현상제거
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
