package heycompany.heychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Inputs
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPasswort;
    private Button mCreateBtn;
    private Button mAccount;

    //Output
    private TextView message;

    //Firebase auth
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Toolba
    private android.support.v7.widget.Toolbar mToolbar;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase auth
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        //register fields
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPasswort = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);
        mAccount = (Button) findViewById(R.id.reg_account);
        message = (TextView) findViewById(R.id.message);

        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent account_intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(account_intent);
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPasswort.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    if(password.length() >= 6) {

                        mRegProgress.setTitle("Registering User");
                        mRegProgress.setMessage("Please wait while we create your Account!");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();

                        registerUser(display_name, email, password);
                    }
                    else{
                        message.setText("The password must contains at least 6 Characters");

                    }
                }
                else{
                    message.setText("Please fill in all fields");
                }
            }
        });
    }

    private void registerUser(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "Hi there. Im using HeyChat.");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", deviceToken);
                    userMap.put("privatePin", "no");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "nur wegen dem dummen pin", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{

                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot Sign in. Please check form or internet connection and try again.", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
