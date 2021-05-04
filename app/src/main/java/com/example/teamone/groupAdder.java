package com.example.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class groupAdder extends Activity {
    View selfLayout;
    Button okB, cancelB;
    ListView listView;
    groupAdderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupadder);
        selfLayout = findViewById(R.id.gAdder);

        EditText gnameTxt = (EditText)selfLayout.findViewById(R.id.txtGname);

        listView = (ListView)selfLayout.findViewById(R.id.groupAdderList);
        adapter = new groupAdderAdapter();

        listView.setAdapter(adapter);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.androids_green),
                "first member") ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupAdderItemList item = (groupAdderItemList) adapter.getItem(position);
                String txt = item.getText();
                Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT).show();
            }
        });

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gName = gnameTxt.getText().toString();
                Intent intent = new Intent(getApplicationContext(), groupList.class);
                intent.putExtra("groupName", gName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelB = (Button)selfLayout.findViewById(R.id.btnCancel);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}