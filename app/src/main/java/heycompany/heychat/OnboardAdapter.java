package heycompany.heychat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnboardAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public OnboardAdapter(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {

            R.drawable.conversations,
            R.drawable.friends,
            R.drawable.moments
    };

    public String[] slide_headings = {

            "CONVERSATIONS",
            "FRIENDS",
            "MOMENTS"
    };

    public String[] slide_descs = {

            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){

        container.removeView((ConstraintLayout) object);
    }
}
