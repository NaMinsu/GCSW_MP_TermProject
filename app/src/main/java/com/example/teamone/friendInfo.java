package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class friendInfo extends Activity {
    HashMap<String, String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendinfo);

        userInfo = FriendListFragment.getInfoTable();
        Intent intent_receive = getIntent();
        String friendName = intent_receive.getStringExtra("friendname");
        String friendMail = userInfo.get((friendName));

        TextView title = findViewById(R.id.fptitle);
        String title_text = friendName + "님의 정보";
        title.setText(title_text);

        ImageView profile = findViewById(R.id.profile_image);

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
}