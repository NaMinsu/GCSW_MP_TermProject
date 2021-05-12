package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    TextView schoolname,nickname,eMails;
    FirebaseDatabase mDatabase;
    DatabaseReference Users;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        View selfLayout = v. findViewById(R.id.miLayout);

        SharedPreferences sf = this.getActivity().getSharedPreferences("Users", MODE_PRIVATE);
        String MY_EMAIL=sf.getString("Email","");
        String[] emailID = MY_EMAIL.split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];

        schoolname = (TextView)selfLayout.findViewById(R.id.schools);
         nickname = (TextView)selfLayout.findViewById(R.id.nicknames);
         eMails = (TextView)selfLayout.findViewById(R.id.Email);

        mDatabase = FirebaseDatabase.getInstance();

        eMails.setText(FirstAuthActivity.getMyID());

        Users = mDatabase.getReference("users").child(DBEmail);
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
                    schoolname.setText(schools);
                }
            }
        });

        Button changeInfo = (Button) selfLayout.findViewById(R.id.btnChangeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), changeInfo.class);
                startActivity(intent);
            }
        });

        ImageView ivProfile = (ImageView)selfLayout.findViewById(R.id.imageView);
        String MYProfile=sf.getString ("profile_image","");
        Glide.with(this)
                .load(MYProfile)
                .override(200,200)
                .circleCrop() // 원으로 깎는거 (원을 원치 않으시면 이줄 지워주세요)
                .into(ivProfile);


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
                Intent logout = new Intent(getActivity(), LoginActivity.class);
                startActivity(logout);
            }
        });


        return v;
    }

    @Override
    public void onResume() { // 프래그먼트가 재시작 하면 새로고침 하게 만들었습니다
        super.onResume();

    }
}
