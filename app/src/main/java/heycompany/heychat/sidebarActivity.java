package heycompany.heychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class sidebarActivity extends AppCompatActivity {

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar);

        navigationView = (NavigationView) findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.logout_drawer:
                        Toast.makeText(sidebarActivity.this, "Logout", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });

    }

    private void sendToStart() {

        Intent startIntent = new Intent(sidebarActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

   /* public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.chats_drawer){

        }
        if(item.getItemId() == R.id.privatechats_drawer){

        }
        if(item.getItemId() == R.id.achievments_drawer){

        }
        if(item.getItemId() == R.id.logout_drawer){
            Log.d("Hallo", "test");
            FirebaseAuth.getInstance().signOut();
            //mUserRef.child("online").setValue("false");
            sendToStart();

        }
        return true;
    }*/
}
