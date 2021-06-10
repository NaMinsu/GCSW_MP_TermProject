package com.gcsw.teamone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class groupDeleter extends Activity {
    View selfLayout;
    Button okB, cancelB;
    String result;
    ArrayList<String> groupItems;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UserGroupRef = database.getReference("UsersGroupInfo");
    DatabaseReference GroupRef = database.getReference("grouplist");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupdeleter);
        selfLayout = findViewById(R.id.gDeleter);
        String MyID = FirstAuthActivity.getMyID();
        EditText gnameTxt = (EditText)selfLayout.findViewById(R.id.txtGname);

        Intent ReceiveIntent = getIntent();
        groupItems = ReceiveIntent.getStringArrayListExtra("GroupData");

        okB = (Button)selfLayout.findViewById(R.id.btnOK);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gName = gnameTxt.getText().toString();
                if(gName.isEmpty()){return;} // 아무것도 입력하지 않으면 중지

                for(String groupData : groupItems){
                    if(groupData.contains(gName)){
                        String Code = groupData.replace("@Admin_split@" + gName,"");
                        UserGroupRef.child(MyID).child(Code).removeValue();
                        GroupRef.child(Code).child("members").child(MyID).removeValue();
                        GroupRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if(!(task.getResult().child(Code).hasChild("members"))){
                                    GroupRef.child(Code).removeValue();
                                }
                            }
                        });
                        result = groupData;
                    }
                }
                    Intent intent = new Intent(getApplicationContext(), FragmentGroupList.class);
                    intent.putExtra("groupInfo", result);
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
