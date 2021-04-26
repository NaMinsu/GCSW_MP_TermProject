package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class settings  extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        View selfLayout = (View) findViewById(R.id.miLayout);
        SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
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