package com.gcsw.teamone;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.ProgressView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class changeInfo extends AppCompatActivity {
    private static final String TAG = "changeInfo";
    private StorageReference mStorageRef; // A profile pictures URL save to mStorageRef
    FirebaseDatabase database;
    int REQUEST_IMAGE_CODE=1001; //Code for image change
    Button btnNickname,btnSchool,btnBack;
    MediaPlayer mediaPlayer;
    ImageView ChangeImage;
    String Nickname,School;
    EditText etNickname,etSchool;
    ProgressView Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        View selfLayout = (View) findViewById(R.id.ciLayout);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        etNickname = (EditText)selfLayout.findViewById(R.id.nickValue);
        etSchool = (EditText)selfLayout.findViewById(R.id.schoolValue);
        Progress = (ProgressView) selfLayout.findViewById(R.id.progress_circular);
        mediaPlayer = MediaPlayer.create(this, R.raw.quietswitch);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        String MY_EMAIL=sf.getString("Email","");
        String[] emailID = MY_EMAIL.split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];
        DatabaseReference myRef = database.getReference("users").child(DBEmail);
        Intent Info_intent = getIntent();
        String before_nick = Info_intent.getStringExtra("nickname");
        String before_school = Info_intent.getStringExtra("school");
        etNickname.setText(before_nick);
        etSchool.setText(before_school);

        ChangeImage = (ImageView) selfLayout.findViewById(R.id.btn_Change_Image);
        String MYProfile=sf.getString ("profile_image","");
        Glide.with(this)
                .load(MYProfile)
                .override(200,200)
                .circleCrop()
                .into(ChangeImage);

        //change image
        ChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });

        //go to setting fragment. fragment "2" is setting fragment
        btnBack = (Button)selfLayout.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment","2");
                startActivity(intent);
            }
        });

        //change nickname and store changing nickname in DB
        btnNickname = (Button)selfLayout.findViewById(R.id.btnNicknameChange);
        btnNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nickname = String.valueOf (etNickname.getText());
                if(Nickname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    mediaPlayer.start();
                    myRef.child("nickname").setValue(Nickname);
                    etNickname.setText("");
                    Toast.makeText(getApplicationContext(), "닉네임이 변경되었습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //change school name and store changing school in DB
        School = String.valueOf(etSchool.getText());
        btnSchool = (Button)selfLayout.findViewById(R.id.btnSchoolChange);
        btnSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                School = String.valueOf (etSchool.getText());
                if(School.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "학교정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    mediaPlayer.start();
                    myRef.child("school").setValue(School);
                    etSchool.setText("");
                    Toast.makeText(getApplicationContext(), "학교가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        String MY_EMAIL=sf.getString("Email",""); //Getting an email from SharedPreferences
        StorageReference profile_Ref = mStorageRef.child("users");
        if(requestCode==REQUEST_IMAGE_CODE && null != data){
            Progress.start();
            Uri image =data.getData();
            String[] emailID = MY_EMAIL.split("\\.");
            String DBEmail = emailID[0]+"_"+emailID[1];
            DatabaseReference myRef = database.getReference("users").child(DBEmail);

            DateFormat dateformat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss", Locale.KOREAN);
            Calendar c = Calendar.getInstance();
            String datetime = dateformat.format(c.getTime());

            profile_Ref.child(DBEmail).child(datetime).child("profile.jpg").putFile(image) // First, upload the file to storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            profile_Ref.child(DBEmail).child(datetime).child("profile.jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "다운받기는 성공" + uri.toString());
                                            String stUri_Image = uri.toString();
                                            myRef.child("profile_image").setValue(stUri_Image);
                                            /*If the upload is successful,
                                              it will receive the image address and save it in DB.*/

                                            editor.putString("profile_image",stUri_Image);
                                            editor.commit(); //It can also be stored in the user's device, SF.
                                            Progress.stop();

                                            Glide.with(getApplicationContext())
                                                    .load(stUri_Image)
                                                    .override(170,170)
                                                    .circleCrop()
                                                    .into(ChangeImage);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                      Log.d(TAG, "다운받기는 실패".toString());
                                       Progress.stop();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                     Log.d(TAG, "올리기 실패".toString());
                    Progress.stop();
                }
            });

        }
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
