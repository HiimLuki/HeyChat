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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PinActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    //Text and Btn
    private TextInputLayout mPin;
    private Button mSaveBtn;

    //User Data
    private String privatePin;

    //Firebase
    private DatabaseReference mDatabase;
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

        setContentView(R.layout.activity_pin);

        //Toolbar
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.pin_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Private Pin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Get Id of the current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Get string from intent
        privatePin = getIntent().getStringExtra("privatePin");

        //get Input from XMLs
        mPin = (TextInputLayout) findViewById(R.id.pin_input);
        mSaveBtn = (Button) findViewById(R.id.save_btn);



        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = mPin.getEditText().getText().toString();

                if(!(pin.equals(privatePin))) {
                    if (pin.length() >= 6) {


                        //Shows Dialog while safing
                        mProgress = new ProgressDialog(PinActivity.this);
                        mProgress.setTitle("Saving Changes");
                        mProgress.setMessage("Wait until changes have been saved");
                        mProgress.show();

                        mDatabase.child("privatePin").setValue(pin).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgress.dismiss();
                                } else {
                                    Toast.makeText(PinActivity.this, "There were some errors while saving", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(PinActivity.this, "Your Pin must have at least 6 characters", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(PinActivity.this, "There were no changes to safe", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
