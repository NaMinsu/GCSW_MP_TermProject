package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstAuthActivity extends AppCompatActivity {
    private Intent intent;
    private static String myID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sf = getSharedPreferences("Users",MODE_PRIVATE);

        if( sf.getString("Email","").length() == 0) {
            // call Login Activity
            intent = new Intent(FirstAuthActivity.this, LoginActivity.class);
        } else {
            // Call Next Activity
            myID = sf.getString("Email", "").replace(".", "_");
            intent = new Intent(FirstAuthActivity.this, MainActivity.class);
        }
        intent.putExtra("fragment","0");
        startActivity(intent);
        this.finish();
    }

    public static String getMyID() { return myID; }
}
