package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

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
                                    FirebaseDatabase.getInstance().getReference("users").child(myID).child("token");
                            usersToken_ref.setValue(token);

                        }
                    });
        }
        intent.putExtra("fragment","0");
        startActivity(intent);
        this.finish();
    }

    public static String getMyID() { return myID; }
}
