package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class settings  extends AppCompatActivity{

    FirebaseDatabase mDatabase;
    DatabaseReference Users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        View selfLayout = (View) findViewById(R.id.miLayout);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
        String MY_EMAIL=sf.getString("Email","");
        String[] emailID = MY_EMAIL.split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];

        TextView nickname = (TextView)findViewById(R.id.nicknames);
        TextView email = (TextView)findViewById(R.id.Email);
        TextView School = (TextView)findViewById(R.id.schools);



        mDatabase = FirebaseDatabase.getInstance();
        Users = mDatabase.getReference("users").child(DBEmail);

        Users.child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String emails = String.valueOf(task.getResult().getValue());
                    email.setText(emails);
                }
            }
        });


        Users.child("nickname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String nicknames = String.valueOf(task.getResult().getValue());
                    nickname.setText(nicknames);
                }
            }
        });

        Users.child("school").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String schools = String.valueOf(task.getResult().getValue());
                    School.setText(schools);
                }
            }
        });

        Button changeInfo = (Button) selfLayout.findViewById(R.id.btnChangeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), changeInfo.class);
                startActivity(intent);
            }
        });



        Button myPageB = (Button)selfLayout.findViewById(R.id.btnMyPage);
        myPageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button groupB = (Button)selfLayout.findViewById(R.id.btnGroup);
        groupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), groupList.class);
                startActivity(intent);
            }
        });

        Button friendB = (Button)selfLayout.findViewById(R.id.btnFriend);
        friendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), friendList.class);
                startActivity(intent);
            }
        });

        Button btnLogout = (Button) selfLayout.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sf.edit();
                editor.putString("Email", null);
                editor.putString("profile_image",null);
                editor.commit();
                // 기기에 저장했던 유저 정보를 지웁니다.
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logout);
            }
        });
        Button settingB = (Button)selfLayout.findViewById(R.id.btnSetUp);

        ImageView ivProfile = (ImageView)selfLayout.findViewById(R.id.imageView);
        String MYProfile=sf.getString ("profile_image","");
        Glide.with(this)
                .load(MYProfile)
                .override(200,200)
                .circleCrop() // 원으로 깎는거 (원을 원치 않으시면 이줄 지워주세요)
                .into(ivProfile);

    }
}