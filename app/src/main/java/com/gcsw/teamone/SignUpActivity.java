 package com.gcsw.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
    MediaPlayer mediaPlayer;
    EditText idText;
    EditText passwordText;
    EditText passwordCheckText;
    Button signUpBtn;

    String ID, PW, PWCheck;
    String TAG = "SignUpActivity";
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
        mediaPlayer = MediaPlayer.create(this, R.raw.thik);
        signUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                // ID, password, password verification string
                ID = idText.getText().toString();
                PW = passwordText.getText().toString();
                PWCheck = passwordCheckText.getText().toString();

                /*
                1. If the password and password confirmation are the same, try to sign up and move on to the login screen.
                2. Toast notification if password and password verification are different
                 */
                if (PW.equals(PWCheck)) {
                    if (ID.length() != 0 && PW.length() != 0) {

                        //Attempt to sign up for membership
                        mAuth.createUserWithEmailAndPassword(ID, PW) //Create User
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's (When membership is successful)
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            // updateUI(user);

                                            String[] emailID = ID.split("\\.");
                                            String DBEmail = emailID[0]+"_"+emailID[1];
                                            //lili13245@naver.com -> lili13245@naver_com Saved as a format.
                                            DatabaseReference myRef = database.getReference("users").child(DBEmail);
                                            Hashtable<String, String> Message_log
                                                    = new Hashtable<String, String>();
                                            String UserID =user.getEmail();
                                            Message_log.put("email",UserID);
                                            Message_log.put("nickname",UserID); // Default Nickname Settings
                                            myRef.setValue(Message_log);
                                            MakeBasicProfile(DBEmail, myRef);
                                            // Creating a default profile
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
                                            // If sign in fails, display a message to the user.
                                             Log.w(TAG, "createUserWithEmail:failure", task.getException());
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
                .load("https://ifh.cc/g/sephR3.png") // Code that loads the image (free image hosting) expired in December, uploads it to Storage, and receives the link.
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
                                                         Log.d(TAG, "다운받기 성공" + uri.toString());
                                                        String stUri_Image = uri.toString();
                                                        myRef.child("profile_image").setValue(stUri_Image);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                  Log.d(TAG, "다운받기 실패".toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                 Log.d(TAG, "올리기 실패".toString());
                            }
                        });
                    }


                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }
    private void killMediaPlayer(){
        if(mediaPlayer!=null){
            try{mediaPlayer.release();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        killMediaPlayer();
    }
}