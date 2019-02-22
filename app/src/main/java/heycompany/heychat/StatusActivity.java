package heycompany.heychat;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSavebtn;


    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leiste unsichtbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String status_value = getIntent().getStringExtra("status_value");

        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = mStatus.getEditText().getText().toString();

                if(!(status.equals(status_value))) {

                    //Progress
                    mProgress = new ProgressDialog(StatusActivity.this);
                    mProgress.setTitle("Saving Changes");
                    mProgress.setMessage("Wait until changes have been saved");
                    mProgress.show();


                    mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                            } else {
                                Toast.makeText(StatusActivity.this, "There were some errors while saving", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(StatusActivity.this, "There are no changes to safe", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
