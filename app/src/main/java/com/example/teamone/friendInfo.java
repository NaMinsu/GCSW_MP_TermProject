package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class friendInfo extends Activity {
    HashMap<String, String> userInfo;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users");
    DatabaseReference friendRef = database.getReference("friendship");
    String friendName;
    String link_image;
    String friendMail;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendinfo);

        Intent intent_rec = getIntent();
        String fname_temp = intent_rec.getStringExtra("friendName");

        friendRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot data = task.getResult();
                    for (DataSnapshot ds : data.getChildren()) {
                        if (ds.child("name").equals(fname_temp)) {
                            friendMail = ds.child("email").getValue().toString();
                        }
                    }
                }
            }
        });

        userRef.child(friendMail.replace('.', '_')).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                DataSnapshot info = task.getResult();
                if (info != null) {
                    friendName = info.child("nickname").getValue().toString();
                    link_image = info.child("profile_image").getValue().toString();
                }
            }
        });

        TextView title = findViewById(R.id.fptitle);
        String title_text = friendName + "님의 정보";
        title.setText(title_text);

        profile = findViewById(R.id.profile_image);
        new DownloadFilesTask().execute(link_image);

        TextView fName = findViewById(R.id.fpname);
        String name_text = "이름: " + friendName;
        fName.setText(name_text);

        TextView fid = findViewById(R.id.fpid);
        String id_text = "E-mail: " + friendMail;
        fid.setText(id_text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                URL url = new URL(link_image);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            profile.setImageBitmap(result);
        }
    }
}