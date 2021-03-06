package com.gcsw.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class friendInfo extends Activity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference friendRef = database.getReference("friendship");
    DatabaseReference userRef = database.getReference("users");
    String friendName, link_image, friendMail, friendSchool;
    TextView title, fName, fid,fid2, fschool,fschool2;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendinfo);
        Intent intent_rec = getIntent();
        String fname_temp = intent_rec.getStringExtra("friendname");
        title = findViewById(R.id.fptitle);
        profile = findViewById(R.id.profile_image);
        fName = findViewById(R.id.fpname);
        fid = findViewById(R.id.fpid);
        fid2 = findViewById(R.id.fpid2);
        fschool = findViewById(R.id.fpcollage);
        fschool2 = findViewById(R.id.fpcollage2);

        fschool2.setSelected(true);
        fid2.setSelected(true);

        friendRef.child(FirstAuthActivity.getMyID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String username = ds.child("name").getValue().toString();
                    if (username.equals(fname_temp)) {
                        friendMail = ds.child("email").getValue().toString();
                        String id_text = friendMail;
                        fid2.setText(id_text);
                    }
                }
            }
        });

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                DataSnapshot users = task.getResult();
                if (friendMail != null) {
                    String fid = friendMail.replace('.', '_');
                    DataSnapshot info = users.child(fid);
                    friendName = info.child("nickname").getValue().toString();
                    String title_text = friendName + "?????? ??????";
                    title.setText(title_text);
                    String name_text = "?? ??????: " + friendName;
                    fName.setText(name_text);
                    if (info.child("school").exists())
                        friendSchool = info.child("school").getValue().toString();
                    fschool2.setText(friendSchool);

                    link_image = info.child("profile_image").getValue().toString();
                    profile = findViewById(R.id.profile_image);
                    putImage(link_image);
                     // new DownloadFilesTask().execute(link_image); ???????????? ????????? ?????? ??????
                }

            }
        });
    }

    private void putImage(String link) {
        Glide.with(this)
                .load(link)
                .override(400, 400)
                .into(profile);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }
/*
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

 */
}