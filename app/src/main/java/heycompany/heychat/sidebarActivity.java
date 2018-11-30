package heycompany.heychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class sidebarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar);


        Button logout_btn = (Button)findViewById(R.id.logout_btn);
        logout_btn.setClickable(true);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHAT_LOG", "Error");
                //FirebaseAuth.getInstance().signOut();
                //mUserRef.child("online").setValue("false");
                //sendToStart();
            }
        });

        Button chat_btn = (Button)findViewById(R.id.chat_btn);

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hallo", "test");
            }
        });

    }

    private void sendToStart() {

        Intent startIntent = new Intent(sidebarActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }
}
