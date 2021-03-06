package heycompany.heychat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import tyrantgit.explosionfield.ExplosionField;

public class StartActivity extends AppCompatActivity {

    //Register und Login Button
    private Button mRegBtn;
    private Button mLoginBtn;
    private ImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leiste unsichtbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_start);

        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mLoginBtn = (Button) findViewById(R.id.start_login_btn);
        mIcon = (ImageView) findViewById(R.id.Icon);

        final ExplosionField explosionField = ExplosionField.attach2Window(this);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                explosionField.explode(mIcon);

                new Timer().schedule(
                        new TimerTask(){

                            @Override
                            public void run(){

                                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                                startActivity(reg_intent);
                            }

                        }, 900);


            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                explosionField.explode(mIcon);

                new Timer().schedule(
                        new TimerTask(){

                            @Override
                            public void run(){

                                Intent log_intent = new Intent(StartActivity.this,LoginActivity.class);
                                startActivity(log_intent);
                            }

                        }, 900);
            }
        });
    }
}
