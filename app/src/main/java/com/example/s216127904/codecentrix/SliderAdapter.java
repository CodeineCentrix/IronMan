package com.example.s216127904.codecentrix;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by s216127904 on 2018/10/17.
 */

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context)
    {
        this.context = context;
    }

    //Arrays
    public int[] slide_images = { R.drawable.step2,
                                    R.drawable.step3,
                                    R.drawable.step4,
                                    R.drawable.step65,
                                    R.drawable.step6 ,
                                    R.drawable.step7,
                                    R.drawable.step1};
    public String[] slide_headings = {"Racer Number",
                                        "Racer Surname",
                                        "Penalty Card",
                                        "Penalty Description",
                                        "Penalty Tent",
                                        "Save",
                                         "Auto Camera"};
    public String[] slide_desc = {"After taking a picture of the racer \nEnter the Racer number",
            "Ask the Racers for their surname and enter it",
            "Select the ticket for penalty done by racer",
            "Select the penalty description ",
            "Select a Tent the penalty will be served in",
            "Click save button to save the penalty",
            "You can set the camera to open automatically \nwhen opening the app and after saving"};

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imgView = (ImageView) view.findViewById(R.id.imgViewLocation);
        TextView heading = (TextView) view.findViewById(R.id.txtViewHead);
        TextView description = (TextView) view.findViewById(R.id.txtViewDesc);

        imgView.setImageResource(slide_images[position]);
        heading.setText(slide_headings[position]);
        description.setText(slide_desc[position]);
        description.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        heading.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        container.addView(view);

        return view;
    };

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
