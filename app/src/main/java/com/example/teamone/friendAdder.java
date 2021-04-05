package com.example.teamone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class friendAdder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendadder);

        RecyclerView fView = (RecyclerView)findViewById(R.id.rcViewFriend);
        fView.setHasFixedSize(true);
    }
}