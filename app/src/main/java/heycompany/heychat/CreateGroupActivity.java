package heycompany.heychat;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {

    private TextInputLayout groupname;
    private Button add_Btn;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        //mRootRef = FirebaseDatabase.getInstance().getReference();

        final String status_value = getIntent().getStringExtra("status_value");
        groupname = (TextInputLayout) findViewById(R.id.groupname);
        add_Btn = (Button) findViewById(R.id.addgroup);

        groupname.getEditText().setText(status_value);

        add_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = groupname.getEditText().getText().toString();

                if(TextUtils.isEmpty(group)){
                    Toast.makeText(CreateGroupActivity.this, "Please write your group name", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroup(group, current_uid);

                    Intent addGroupIntent = new Intent(CreateGroupActivity.this,AddToGroupActivity.class);
                    addGroupIntent.putExtra("groupname",group);
                    startActivity(addGroupIntent);
                }
            }
        });


    }

    private void CreateNewGroup(String group, String current_uid) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(group).child("member").child(current_uid);

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("rank", "admin");

        mDatabase.setValue(userMap);
        //mDatabase.child("Groups").child(group).child("member").child(current_uid).setValue("admin");
    }
    private void AddToGroup(){

    }
    private void setImage(){

    }
}
