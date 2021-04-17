 package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    private StorageReference mStorageRef; // 기본 프로필 사진 등록을 위한 mStorageRef
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText idText;
    EditText passwordText;
    EditText passwordCheckText;
    Button signUpBtn;

    String ID, PW, PWCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        idText = (EditText) findViewById(R.id.signUpId);
        passwordText = (EditText) findViewById(R.id.signUpPW);
        passwordCheckText = (EditText) findViewById(R.id.signUpPWCheck);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        mStorageRef = FirebaseStorage.getInstance().getReference();
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
                    if (ID.length() != 0 && PW.length() != 0) {

                        //회원가입 시도
                        mAuth.createUserWithEmailAndPassword(ID, PW) //유저 만들기
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's (회원가입 성공시)
                                            //Log.d(TAG, "createUserWithEmail:success"); Tag 설정 해주신후 주석해제 해주세요 !
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            //    updateUI(user);
                                            String[] emailID = ID.split("\\.");
                                            String DBEmail = emailID[0]+"_"+emailID[1];
                                            //lili13245@naver.com -> lili13245@naver_com 형식으로 저장됩니다.
                                            DatabaseReference myRef = database.getReference("users").child(DBEmail);
                                            Hashtable<String, String> Message_log
                                                    = new Hashtable<String, String>();

                                            Message_log.put("email", user.getEmail());
                                            myRef.setValue(Message_log);
                                            MakeBasicProfile(DBEmail, myRef);
                                            // 기본 프로필을 만드는 것

                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignUpActivity.this, "해당하는 이메일로 인증메일을 보냈습니다. 인증을 완료하시면 가입이 완료됩니다", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                            Intent Exit = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(Exit);
                                        } else {
                                            // If sign in fails, display a message to the user.(회원가입 실패시)
                                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            //   updateUI(null);
                                        }

                                    }
                                });

                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "Check your Password and Password Check", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void MakeBasicProfile(String RefName, DatabaseReference myRef) {
        StorageReference profile_Ref = mStorageRef.child("users");
        DateFormat dateformat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss", Locale.KOREAN);
        Calendar c = Calendar.getInstance();
        String datetime = dateformat.format(c.getTime());
        Glide.with(getApplicationContext()).asFile()
                .load("https://ifh.cc/g/sephR3.png") // 12월에 만료 (무료 이미지 호스팅) 이미지를  load해서 Storage 에 올린후 그 링크를 받는 코드입니다
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        Uri basic_profile = Uri.fromFile(new File(String.valueOf(resource)));

                        profile_Ref.child(RefName).child(datetime).child("profile.jpg").putFile(basic_profile)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        profile_Ref.child(RefName).child(datetime).child("profile.jpg").getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        // Log.d(TAG, "다운받기는 성공" + uri.toString());
                                                        String stUri_Image = uri.toString();
                                                        myRef.child("profile_image").setValue(stUri_Image);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                //  Log.d(TAG, "다운받기는 실패".toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Log.d(TAG, "올리기 실패".toString());
                            }
                        });
                    }


                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }
}