package heycompany.heychat;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private Button resetBtn;
    private TextInputLayout emailAddress;
    private String email;

    private android.support.v7.widget.Toolbar mToolbar;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.reset_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reset password");

        resetBtn = (Button)findViewById(R.id.resetBtn);
        emailAddress = (TextInputLayout)findViewById((R.id.resetEmail));

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailAddress.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email)) {

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(PasswordResetActivity.this, "Email send", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

               }
                else{
                    Toast.makeText(PasswordResetActivity.this, "Please enter your email address", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
