package com.example.teamone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        View selfLayout = v. findViewById(R.id.miLayout);
        SharedPreferences sf = this.getActivity().getSharedPreferences("Users", MODE_PRIVATE);
        Button changeInfo = (Button) selfLayout.findViewById(R.id.btnChangeInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), changeInfo.class);
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
                Intent logout = new Intent(getActivity(), LoginActivity.class);
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

        return v;
    }

}