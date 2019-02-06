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
import java.util.Random;

public class CreateGroupActivity extends AppCompatActivity {

    private TextInputLayout groupname;
    private Button add_Btn;

    //Firebase
    private DatabaseReference mDatabaseInfo;
    private DatabaseReference mDatabaseMember;
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

        /*Random rand = new Random();
        int a = rand.nextInt(9);
        int b = rand.nextInt(9);
        int c = rand.nextInt(9);
        int d = rand.nextInt(9);
        int e = rand.nextInt(9);
        int f = rand.nextInt(9);
        String random = String.valueOf(a) + String.valueOf(b) + String.valueOf(c) + String.valueOf(d) + String.valueOf(e) + String.valueOf(f);
*/
        mDatabaseInfo = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid).child("groupinfo");

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", group);
        userMap.put("admin", current_uid);
        mDatabaseInfo.setValue(userMap);

        mDatabaseMember = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid).child("member").child(current_uid);

        HashMap<String, String> adminMap = new HashMap<>();
        adminMap.put("seen", "false");
        mDatabaseMember.setValue(adminMap);
    }
    private void AddToGroup(){

    }
    private void setImage(){

    }

}