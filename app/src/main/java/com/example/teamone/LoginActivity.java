package com.example.teamone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Hashtable;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    EditText idText;
    EditText pwText;
    Button logInButton;
    TextView signUpText;

    String ID, PW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        idText = (EditText) findViewById(R.id.logInID);
        pwText = (EditText) findViewById(R.id.logInPW);
        logInButton = (Button) findViewById(R.id.logInBtn);
        signUpText = (TextView) findViewById(R.id.singUpTxt);


        /*
        If the Login button is clicked
         */
        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ID = String.valueOf(idText.getText());
                PW = String.valueOf(pwText.getText());

                if (ID.length() != 0 && PW.length() != 0) {

                    mAuth.signInWithEmailAndPassword(ID, PW)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) { //Login Successful
                                        Log.d("LoginActivity", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user.isEmailVerified()) {  // Email Authentication Check
                                            String stUserEmail = user.getEmail();
                                            SharedPreferences sf = getSharedPreferences("Users", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sf.edit();
                                            editor.putString("Email", stUserEmail);
                                            editor.commit();
                                            /*https://jhshjs.tistory.com/56  SharedPreferences
                                             Data stored on the device until the user clears the app
                                            Save email when user log in, from the app launch screen.
                                            After checking the user's information, if there is email information in the sf, it is made to skip login.*/

                                            String[] emailID = stUserEmail.split("\\.");
                                            String DBEmail = emailID[0] + "_" + emailID[1];
                                            DatabaseReference users_ref = database.getReference("users").child(DBEmail);
                                            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    // Log.d(TAG, "onDataChange: " + snapshot.getValue().toString());
                                                    User_Item nicknameCamera = snapshot.getValue(User_Item.class);
                                                    /*Image URL was loaded when logging in to reduce the time to load profile pictures.*/

                                                    editor.putString("profile_image", nicknameCamera.getProfile_image());
                                                    editor.commit();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                                            Intent in = new Intent(LoginActivity.this, FirstAuthActivity.class); //Always do the examination
                                            startActivity(in);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "이메일 인증을 완료해주세요.", Toast.LENGTH_LONG).show();
                                        }
                                    } else { // When login fails to log in
                                          Log.w("LoginActivity", "signInWithEmail:failure", task.getException()); //Failure Log Part
                                        Toast.makeText(LoginActivity.this, "Check your ID or Password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });


        /*
        if the signupText is clicked
         */
        signUpText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //   updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        //Code to prevent return to previous Activity when login screen back button
        moveTaskToBack(true);
    }


}
