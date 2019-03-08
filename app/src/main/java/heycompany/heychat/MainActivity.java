package heycompany.heychat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    //Buttons for Left Drawer
    Button logoutBtn;
    Button ChatsBtn;
    Button PrivBtn;
    Button AchievmentsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leiste unsichtbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

       mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
       mToolbar.setTitle("Chats");
       mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
       setSupportActionBar(mToolbar);

       //SlideMenu
        new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(mToolbar)
                .withDragDistance(260)
                .withRootViewScale(0.82f)
                .withMenuOpened(false)
                .withMenuLayout(R.layout.activity_sidebar)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        //ChangeBurgerSymbol
        //mToolbar.setNavigationIcon(R.drawable.burger);
        //mToolbar.setNavigationIcon(R.drawable.burgermessage);

        if(mAuth.getCurrentUser()!=null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

       //Tabs

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //Buttons for the LeftDrawer
        ChatsBtn = (Button) findViewById(R.id.chat_btn);
        ChatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        PrivBtn = (Button) findViewById(R.id.privatechat_btn);
        PrivBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        AchievmentsBtn = (Button) findViewById(R.id.achievments_btn);
        AchievmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent OnboardIntent = new Intent(MainActivity.this,Onboarding.class);
                startActivity(OnboardIntent);
                finish();

            }
        });

        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //mUserRef.child("online").setValue("false");
                sendToStart();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();
        }else{
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            mUserRef.child("online").setValue(System.currentTimeMillis());
            //Timestamp server value
            //ServerValue.TIMESTAMP
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_btn){

            FirebaseAuth.getInstance().signOut();
            //mUserRef.child("online").setValue("false");
            sendToStart();
        }
        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.all_users_btn){

            Intent UsersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(UsersIntent);

        }
        if(item.getItemId() == R.id.groups_btn){

            Intent GroupIntent = new Intent(MainActivity.this, CreateGroupActivity.class);
            startActivity(GroupIntent);

        }
        return true;
    }
}
