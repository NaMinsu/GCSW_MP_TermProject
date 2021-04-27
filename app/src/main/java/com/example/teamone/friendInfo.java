package com.example.teamone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

public class friendInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendinfo);

        Intent intent_receive = getIntent();
        String friendName = intent_receive.getStringExtra("friendname");

        TextView title = findViewById(R.id.fptitle);
        String title_text = "친구 정보: " + friendName;
        title.setText(title_text);

        TextView fName = findViewById(R.id.fpname);
        String name_text = "이름: " + friendName;
        fName.setText(name_text);

        TextView fid = findViewById(R.id.fpid);
        String id_text = "E-mail: ";
        fid.setText(id_text);

        TextView fcol = findViewById(R.id.fpcollage);
        String collage_text = "Collage: ";
        fcol.setText(collage_text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }
}