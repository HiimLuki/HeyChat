package heycompany.heychat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PrivateLoginActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_login);

        //Toolbar
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.privatLogin_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login Private Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Id of the current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
    }
}
