package heycompany.heychat;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Onboarding extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private Button getinBtn;

    private int mCurrentPage;

    private OnboardAdapter onboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leiste unsichtbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_onboarding);

        mSlideViewPager = (ViewPager) findViewById(R.id.slide_viewpager);
        mDotLayout = (LinearLayout) findViewById(R.id.dots_layout);
        getinBtn = (Button) findViewById(R.id.btn_next);

        onboardAdapter = new OnboardAdapter(this);
        mSlideViewPager.setAdapter(onboardAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        getinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(Onboarding.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    public void addDotsIndicator(int position){
        mDotLayout.removeAllViews();
        mDots = new TextView[3];

        for(int i = 0; i< mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.white));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.yellow));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);

            mCurrentPage = position;

            if(mCurrentPage == 0){
                getinBtn.setVisibility(View.INVISIBLE);
            }
            else if(mCurrentPage == 1){
                getinBtn.setVisibility(View.INVISIBLE);
            }
            else if(mCurrentPage == 2){
                getinBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
