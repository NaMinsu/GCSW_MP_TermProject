package com.example.teamone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class groupAdder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupadder);

        RecyclerView gView = (RecyclerView)findViewById(R.id.rcViewGroup);
        gView.setHasFixedSize(true);
    }
}