package com.example.teamone;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private StorageReference mStorageRef; // 프로필 사진 등록을 위한 mStorageRef
    FirebaseDatabase database;
    int REQUEST_IMAGE_CODE=1001; //이미지 변경용 코드입니다
    Button btnNickname,btnSchool,btnBack;  // 이미지 변경용 임시 버튼을 만들었습니다
    ImageView ChangeImage;
    String Nickname,School;
    EditText nickname,school;
    ProgressView Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        View selfLayout = (View) findViewById(R.id.ciLayout);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        nickname = (EditText)selfLayout.findViewById(R.id.nickValue);
        school = (EditText)selfLayout.findViewById(R.id.schoolValue);
        Progress = (ProgressView) selfLayout.findViewById(R.id.progress_circular);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        String MY_EMAIL=sf.getString("Email","");
        String[] emailID = MY_EMAIL.split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];
        DatabaseReference myRef = database.getReference("users").child(DBEmail);

        ChangeImage = (ImageView) selfLayout.findViewById(R.id.btn_Change_Image);
        String MYProfile=sf.getString ("profile_image","");
        Glide.with(this)
                .load(MYProfile)
                .override(200,200)
                .circleCrop()
                .into(ChangeImage);

        ChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });

        btnBack = (Button)selfLayout.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment","2");
                startActivity(intent);
            }
        });


        btnNickname = (Button)selfLayout.findViewById(R.id.btnNicknameChange);
        btnNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nickname = String.valueOf (nickname.getText());
                myRef.child("nickname").setValue(Nickname);
                nickname.setText("");
                Toast.makeText(getApplicationContext(),"닉네임이 변경되었습니다!",Toast.LENGTH_SHORT).show();
            }
        });


        School = String.valueOf(school.getText());
        btnSchool = (Button)selfLayout.findViewById(R.id.btnSchoolChange);
        btnSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                School = String.valueOf (school.getText());
                myRef.child("school").setValue(School);
                school.setText("");
                Toast.makeText(getApplicationContext(),"학교가 변경되었습니다!",Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        String MY_EMAIL=sf.getString("Email",""); //SharedPreferences 에서 이메일을 가져오는것
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

            profile_Ref.child(DBEmail).child(datetime).child("profile.jpg").putFile(image) // 먼저 파일을 스토리지에 업로드하고
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            profile_Ref.child(DBEmail).child(datetime).child("profile.jpg").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "다운받기는 성공" + uri.toString());
                                            String stUri_Image = uri.toString();
                                            myRef.child("profile_image").setValue(stUri_Image); //업로드에성공하면 이미지 주소를 받아 DB에 찍어준다
                                            editor.putString("profile_image",stUri_Image);
                                            editor.commit(); //유저의 기기 sf 에도 저장시켜준다
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
}
