package heycompany.heychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PrivateLoginActivity extends AppCompatActivity {



    //GUI Input
    private Button loginBtn;
    private TextInputLayout pinField;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private String pinDatabase;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_login);



        //Get Id of the current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        //DatabaseReference from current user
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        loginBtn = findViewById(R.id.login_btn);
        pinField = (TextInputLayout) findViewById(R.id.pin);

        loginBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String pin = pinField.getEditText().toString();
                if(!TextUtils.isEmpty(pin)){

                    if(pin.equals(pinDatabase)){
                        Toast.makeText(PrivateLoginActivity.this, "Login ", Toast.LENGTH_LONG).show();

                        Intent private_intent = new Intent(PrivateLoginActivity.this, PrivateChatActivity.class);
                        startActivity(private_intent);
                    }

                }else{
                    Toast.makeText(PrivateLoginActivity.this, "Please enter a Pin", Toast.LENGTH_LONG).show();

                }
            }
        });

       mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                pinDatabase = dataSnapshot.child("privatePin").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
